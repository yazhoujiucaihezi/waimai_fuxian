package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
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
        orders.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(15));
        orders.setCheckoutTime(LocalDateTime.now());

        orderMapper.update(orders);

        orderPaymentVO.setEstimatedDeliveryTime(orders.getEstimatedDeliveryTime());
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
    public void cancel(Long id) {
        Orders orders = orderMapper.getById(id);
        orders.setStatus(Orders.CANCELLED);
        orderMapper.update(orders);
    }

    @Transactional
    public void repetition(Long id) {
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
}
