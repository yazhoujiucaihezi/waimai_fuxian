package com.sky.service;

import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO dto);

    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    PageResult pageQuery4User(int page, int pageSize, Integer status);

    void cancel4User(Long id);

    void repetition4User(Long id);

    PageResult pageQuery4Admin(OrdersPageQueryDTO dto);

    OrderStatisticsVO orderStatistics();

    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO ordersRejectionDTO);

    void cancel4Admin(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);

    void complete(Long id);

    void reminder(Long id);
}
