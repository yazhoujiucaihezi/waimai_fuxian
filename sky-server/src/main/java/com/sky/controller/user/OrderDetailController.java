package com.sky.controller.user;

import com.sky.entity.OrderDetail;
import com.sky.mapper.OrderDetailMapper;
import com.sky.result.Result;
import com.sky.service.OrderDetailService;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/order/orderDetail")
@Slf4j
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetailById(@PathVariable("id") Long id) {
        log.info("根据id查询订单详情");
        OrderVO orderVO = orderDetailService.getOrderDetailById(id);
        return Result.success(orderVO);
    }


}
