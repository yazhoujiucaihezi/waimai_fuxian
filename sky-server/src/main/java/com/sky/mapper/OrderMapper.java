package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {


    void insert(Orders orders);

    Orders getBynumber(String number);

    void update(Orders orders);

    Orders getById(Long id);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    Long getNumber();

    Integer getNumberBystatus(Integer status);

    void updateByStatus(@Param("oldStatus") Integer oldStatus, @Param("newStatus") Integer newStatus,
                        @Param("cancelReason") String cancelReason, @Param("cancelTime") LocalDateTime cancelTime,
                        @Param("deliveryTime") LocalDateTime deliveryTime,
                        @Param("orderTime") LocalDateTime orderTime);

    LocalDateTime getOrderTimeBystatus(@Param("unpaidStatus") Integer unpaidStatus);
}
