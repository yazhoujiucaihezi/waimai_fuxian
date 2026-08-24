package com.sky.mapper;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {


    void insert(Orders orders);

    Orders getBynumber(String number);

    void update(Orders orders);

    List<OrderVO> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO);
}
