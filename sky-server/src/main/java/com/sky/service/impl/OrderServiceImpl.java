package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.WebSocket.WebSocketServer;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Value("${sky.shop.address}")
    private String shopAddress;
    @Value("${sky.baidu.ak}")
    private String ak;
    @Autowired
    private WebSocketServer webSocketServer;
    static final Integer DELIVERY_FEE = 6;

    /**
     * 提交订单
     *
     * @param dto
     * @return
     */
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {

        //查询购物车
        List<ShoppingCart> cartList = shoppingCartMapper.list(BaseContext.getCurrentId());

        //查询地址
        AddressBook addressBook = addressBookMapper.getById(dto.getAddressBookId());

        //封装订单信息
        Orders orders = new Orders();
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setUserId(BaseContext.getCurrentId());
        orders.setAddressBookId(dto.getAddressBookId());
        orders.setPayMethod(dto.getPayMethod());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setTablewareNumber(dto.getTablewareNumber());
        orders.setPackAmount(dto.getPackAmount());
        orders.setPhone(addressBook.getPhone());

        String address = addressBook.getProvinceName()
                + addressBook.getCityName()
                + addressBook.getDistrictName()
                + addressBook.getDetail();
        orders.setAddress(address);
        User user = userMapper.getById(BaseContext.getCurrentId());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());

        //计算金额
        BigDecimal amount = BigDecimal.ZERO;
        for (ShoppingCart cart : cartList) {
            amount = amount.add(
                    cart.getAmount().multiply(new BigDecimal(cart.getNumber()))
            );
        }

        //打包费+配送费
        amount = amount.add(new BigDecimal(dto.getPackAmount()));
        amount = amount.add(new BigDecimal(DELIVERY_FEE));

        orders.setAmount(amount);
        orders.setRemark(dto.getRemark());
        orders.setOrderTime(LocalDateTime.now());

        //计算订单距离
        checkOutOfRange(address);

        //保存订单
        orderMapper.insert(orders);

        //保存订单明细
        List<OrderDetail> details = new ArrayList<>();
        for (ShoppingCart cart : cartList) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orders.getId());
            detail.setName(cart.getName());
            detail.setImage(cart.getImage());
            detail.setAmount(cart.getAmount());
            detail.setNumber(cart.getNumber());
            detail.setDishId(cart.getDishId());
            detail.setSetmealId(cart.getSetmealId());
            detail.setDishFlavor(cart.getDishFlavor());
            details.add(detail);
        }
        orderDetailMapper.insertBatch(details);

        //清空购物车
        shoppingCartMapper.clean(BaseContext.getCurrentId());

        //返回订单提交VO
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 支付
     * @param ordersPaymentDTO
     * @return
     */
    @Transactional
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) {
        OrderPaymentVO orderPaymentVO = new OrderPaymentVO();
        Orders orders = new Orders();
        String number = ordersPaymentDTO.getOrderNumber();
        orders = orderMapper.getBynumber(number);
        orders.setPayStatus(Orders.PAID);
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        orders.setPayMethod(1);
        orders.setCheckoutTime(LocalDateTime.now());

        orderMapper.update(orders);

        //来单提醒 type orderId content
        Map map = new HashMap();
        map.put("type",1);
        map.put("orderId",orders.getId());
        map.put("content","订单号：" + number);

        String json = JSON.toJSONString(map);//将map转成JSON字符串
        webSocketServer.sendToAllClient(json); // Send JSON string to all connected clients
        BeanUtils.copyProperties(orders, orderPaymentVO);
        return orderPaymentVO;
    }

    /**
     * 用户端订单分页查询
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 取消订单
     *
     * @param id
     */
    @Transactional
    public void cancel4User(Long id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.CANCELLED);
        orderMapper.update(orders);
    }

    /**
     * 用户端订单重复
     * @param id
     */
    @Transactional
    public void repetition4User(Long id) {
        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getId());
        orderDetails.forEach(orderDetail -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setImage(orderDetail.getImage());
            shoppingCart.setDishId(orderDetail.getDishId());
            shoppingCart.setSetmealId(orderDetail.getSetmealId());
            shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            shoppingCart.setNumber(orderDetail.getNumber());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCartMapper.add(shoppingCart);
        });

    }

    /**
     * 订单分页查询
     * @param dto
     * @return
     */
    public PageResult pageQuery4Admin(OrdersPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(dto);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 订单统计
     * @return
     */
    public OrderStatisticsVO orderStatistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        Orders orders = new Orders();
        orders.setStatus(Orders.CONFIRMED);
        Integer confirmedNumber = orderMapper.getNumberBystatus(orders.getStatus());
        orders.setStatus(Orders.TO_BE_CONFIRMED);
        Integer toBeConfirmedNumber = orderMapper.getNumberBystatus(orders.getStatus());
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer deliveryInProgressNumber = orderMapper.getNumberBystatus(orders.getStatus());
        orderStatisticsVO.setConfirmed(confirmedNumber);
        orderStatisticsVO.setToBeConfirmed(toBeConfirmedNumber);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgressNumber);
        return orderStatisticsVO;
    }

    /**
     * 确认
     * @param ordersConfirmDTO
     */
    @Transactional
    public void confirm(OrdersConfirmDTO ordersConfirmDTO) {
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO, orders);
        orderMapper.update(orders);
    }

    /**
     * 拒绝
     * @param ordersRejectionDTO
     */
    @Transactional
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {
       Orders orders = new Orders();
       orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
       orders.setStatus(Orders.CANCELLED);
       orders.setId(ordersRejectionDTO.getId());
       orders.setCancelTime(LocalDateTime.now());
       orders.setCancelReason("商家拒单");
       orderMapper.update(orders);
    }

    /**
     * 取消
     * @param ordersCancelDTO
     */
    @Transactional
    public void cancel4Admin(OrdersCancelDTO ordersCancelDTO) {
        Orders orders = new Orders();
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setStatus(Orders.CANCELLED);
        orders.setId(ordersCancelDTO.getId());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
     * 配送
     * @param id
     */
    @Transactional
    public void delivery(Long id) {
       Orders orders = new Orders();
       orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
       orders.setId(id);
       orders.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(15));
       orderMapper.update(orders);
    }

    /**
     * 完成
     * @param id
     */
    @Transactional
    public void complete(Long id) {
       Orders orders = new Orders();
       orders.setStatus(Orders.COMPLETED);
       orders.setId(id);
       orders.setDeliveryTime(LocalDateTime.now());
       orderMapper.update(orders);
    }

    /**
     * 催单
     * @param id
     */
    public void reminder(Long id) {
        Map map = new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","用户催单："+"订单号：" + orderMapper.getById(id).getNumber());
        String json = JSON.toJSONString(map);//将map转成JSON字符串
        webSocketServer.sendToAllClient(json); // Send JSON string to all connected clients
    }

    /**
     * 检查客户的收货地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        //获取店铺的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("店铺地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        //店铺经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }

}
