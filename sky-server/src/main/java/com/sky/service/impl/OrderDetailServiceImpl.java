package com.sky.service.impl;

import com.sky.entity.OrderDetail;
import com.sky.mapper.OrderDetailMapper;
import com.sky.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    @Autowired
    private  OrderDetailMapper orderDetailMapper;

    public OrderDetail getOrderDetailById(Long id) {
        return orderDetailMapper.getOrderDetailById(id);
    }
}
