package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Select;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    /**
     * 营业额统计
     */
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> datalist = new ArrayList<>();

        datalist.add(begin);

        while(!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : datalist){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer status = Orders.COMPLETED;
            Double amount = orderMapper.getAmount(beginTime, endTime,status);
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

        while(!begin.isEqual(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }

        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : datalist){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Integer newUserCount = orderMapper.getUserCount(map);
            newUserList.add(newUserCount);
        }

        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : datalist){
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
}
