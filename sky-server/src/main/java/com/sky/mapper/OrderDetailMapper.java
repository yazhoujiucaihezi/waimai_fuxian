package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    void insertBatch(@Param("list") List<OrderDetail> details);

    List<OrderDetail> getOrderDetailById(@Param("id") Long id);

    List<OrderDetail> getByOrderId(Long id);
}
