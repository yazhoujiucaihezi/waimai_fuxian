package com.sky.service;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO dto);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    PageResult pageQuery4User(int page, int pageSize, Integer status);

    void cancel(Long id);

    void repetition(Long id);
}
