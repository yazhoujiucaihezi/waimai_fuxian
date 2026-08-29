package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public BusinessDataVO getBusinessData() {

        // 获取当前日期
        LocalDate now = LocalDate.now();
        LocalDateTime beginTime = now.atStartOfDay();
        LocalDateTime endTime = now.atTime(23, 59, 59);

        Integer status = Orders.COMPLETED;

        Map map = new HashMap();
        map.put("beginTime", beginTime);
        map.put("endTime", endTime);

        // 查询总订单数
        Integer totalOrderCount = orderMapper.getOrderCount(map);

        // 新用户数量
        Integer newUsers = orderMapper.getUserCount(map);

        map.put("status", status);

        // 营业额统计
        Double amount = orderMapper.getAmount(beginTime, endTime, status);
        amount = amount == null ? 0.0 : amount;

        // 有效订单数
        Integer validOrderCount = orderMapper.getOrderCount(map);

        // 订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = (double) validOrderCount / totalOrderCount;
        }

        // 平均客单价
        Double unitPrice = 0.0;
        if (validOrderCount != 0) {
            unitPrice = Math.round((amount / validOrderCount) * 100) / 100.0;
        }

        return BusinessDataVO.builder()
                .turnover(amount)
                .validOrderCount(validOrderCount)
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    public OrderOverViewVO getOrderOverView() {
        Map map = new HashMap();
        Integer allOrders = orderMapper.getOrderCount(map);
        Integer status = Orders.CANCELLED;
        Integer cancaledOrders = orderMapper.getOrderCountByStatus(status);
        status = Orders.COMPLETED;
        Integer completedOrders = orderMapper.getOrderCountByStatus(status);
        status = Orders.DELIVERY_IN_PROGRESS;
        Integer deliveredOrders = orderMapper.getOrderCountByStatus(status);
        status = Orders.TO_BE_CONFIRMED;
        Integer waitingOrders = orderMapper.getOrderCountByStatus(status);
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancaledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    public DishOverViewVO getDishOverView() {
        Integer status = StatusConstant.DISABLE;
        Integer discontinued = dishMapper.countDishByStatus(status);
        status = StatusConstant.ENABLE;
        Integer sold = dishMapper.countDishByStatus(status);
        return DishOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    public SetmealOverViewVO getSetmealOverView() {
        Integer status = StatusConstant.DISABLE;
        Integer discontinued = setmealMapper.countSetmealByStatus(status);
        status = StatusConstant.ENABLE;
        Integer sold = setmealMapper.countSetmealByStatus(status);
        return SetmealOverViewVO.builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }
}
