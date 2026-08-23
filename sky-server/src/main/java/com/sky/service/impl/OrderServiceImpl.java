package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

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
    public OrderSubmitVO submit(OrdersSubmitDTO dto) {
        List<ShoppingCart> cartList = shoppingCartMapper.list(BaseContext.getCurrentId());
        Orders orders = new Orders();
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setUserId(BaseContext.getCurrentId());
        orders.setAddressBookId(dto.getAddressBookId());
        orders.setPayMethod(dto.getPayMethod());
        orders.setStatus(Orders.PENDING_PAYMENT);

        BigDecimal amount = BigDecimal.ZERO;
        for(ShoppingCart cart : cartList){
            amount = amount.add(
                    cart.getAmount().multiply(new BigDecimal(cart.getNumber()))
            );
        }
        orders.setAmount(amount);

        orders.setRemark(dto.getRemark());
        orders.setOrderTime(LocalDateTime.now());
        orderMapper.insert(orders);
        List<OrderDetail> details = new ArrayList<>();
        for(ShoppingCart cart : cartList){
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(orders.getId());
            detail.setName(cart.getName());
            detail.setImage(cart.getImage());
            detail.setAmount(cart.getAmount());
            detail.setNumber(cart.getNumber());

            details.add(detail);
        }
        orderDetailMapper.insertBatch(details);


        shoppingCartMapper.clean(BaseContext.getCurrentId());
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    @Override
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
        orderMapper.update(orders);
        orderPaymentVO.setEstimatedDeliveryTime(orders.getEstimatedDeliveryTime());
        return orderPaymentVO;
    }
}
