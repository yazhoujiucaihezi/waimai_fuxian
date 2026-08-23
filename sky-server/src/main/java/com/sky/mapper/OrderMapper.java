package com.sky.mapper;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderSubmitVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {


    void insert(Orders orders);

    Orders getBynumber(String number);

    void update(Orders orders);
}
