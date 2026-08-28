package com.sky.service.impl;

import com.sky.dto.OrdersDTO;
import com.sky.entity.Dish;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 营业额统计
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> datalist = new ArrayList<>();

        datalist.add(begin);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : datalist) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer status = Orders.COMPLETED;
            Double amount = orderMapper.getAmount(beginTime, endTime, status);
            amount = amount == null ? 0.0 : amount;
            turnoverList.add(amount);
        }


        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(datalist, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }


    /**
     * 用户统计
     */
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datalist = new ArrayList<>();

        datalist.add(begin);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : datalist) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer newUserCount = orderMapper.getUserCount(map);
            newUserList.add(newUserCount);
        }

        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : datalist) {
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("endTime", endTime);
            Integer totalUserCount = orderMapper.getUserCount(map);
            totalUserList.add(totalUserCount);
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(datalist, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> datalist = new ArrayList<>();

        datalist.add(begin);

        while (!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }

        List<Integer> orderCountList = new ArrayList<>();
        for (LocalDate date : datalist) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer totalOrderCount = orderMapper.getOrderCount(map);
            orderCountList.add(totalOrderCount);
        }

        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : datalist) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.getOrderCount(map);
            validOrderCountList.add(validOrderCount);
        }

        Double orderCompletionRate = (double) validOrderCountList.stream().mapToInt(Integer::intValue).sum() / orderCountList.stream().mapToInt(Integer::intValue).sum();
        orderCompletionRate = orderCompletionRate != 0 ? orderCompletionRate : 0;

        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(datalist, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 销量排名
     */
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        Integer status = Orders.COMPLETED;

        // 查询时间范围内已完成订单的id
        List<Long> idList = orderMapper.getIdByStatus(status, begin, end);

        // 菜品id -> 销量
        Map<Long, Integer> dishMap = new HashMap<>();

        // 统计销量
        for (Long orderId : idList) {

            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);

            for (OrderDetail orderDetail : orderDetailList) {

                Long dishId = orderDetail.getDishId();

                if (dishId != null) {

                    Integer number = dishMap.get(dishId);

                    if (number == null) {
                        dishMap.put(dishId, orderDetail.getNumber());
                    } else {
                        dishMap.put(dishId, number + orderDetail.getNumber());
                    }
                }
            }
        }

        // 按销量从高到低排序
        List<Map.Entry<Long, Integer>> list =
                new ArrayList<>(dishMap.entrySet());

        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // 前10名
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();

        for (int i = 0; i < list.size() && i < 10; i++) {

            Long dishId = list.get(i).getKey();
            Integer number = list.get(i).getValue();

            DishVO dishVO = dishMapper.getDishById(dishId);

            nameList.add(dishVO.getName());
            numberList.add(number);
        }

        // 返回结果
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }
}
