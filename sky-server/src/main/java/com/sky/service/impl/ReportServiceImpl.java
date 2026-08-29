package com.sky.service.impl;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private WorkSpaceService workSpaceService;
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

    /**
     * 导出运营数据报表
     * @param response
     */
    public void export(HttpServletResponse response) {
        //查询数据库，获取营业数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        LocalDateTime begin= LocalDateTime.of(dateBegin, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(dateEnd, LocalTime.MAX);
        BusinessDataVO businessData = workSpaceService.getBusinessData(begin, end);

        //通过poi将数据写入excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创造新的excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间:" + dateBegin + "至" + dateEnd);
            //获取第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getValidOrderCount());
            row.getCell(6).setCellValue(businessData.getNewUsers());

            //获取第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            //填充明细数据
            for (int i = 0;i < 30; i++){
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessDataVO = workSpaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessDataVO.getTurnover());
                row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            }

            //通过输出流将excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            excel.close();
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
