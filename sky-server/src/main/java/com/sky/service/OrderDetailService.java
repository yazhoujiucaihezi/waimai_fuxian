package com.sky.service;

import com.sky.entity.OrderDetail;
import com.sky.vo.OrderVO;

public interface OrderDetailService {
    OrderVO getOrderDetailById(Long id);
}
