package com.sky.Task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@EnableScheduling
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    /**
     * 定时取消未付款订单
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void orderCancel() {
        System.out.println("取消未付款订单");
        Integer UnpaidStatus = Orders.PENDING_PAYMENT;
        Integer CancelStatus = Orders.CANCELLED;
        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(15);
        LocalDateTime cancelTime = LocalDateTime.now();
        String cancelReason = "订单付款超时取消";
        System.out.println(orderTime);
        orderMapper.updateByStatus(UnpaidStatus,CancelStatus,cancelReason,cancelTime,null,orderTime);
    }

    /**
     * 定时确认订单
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void orderConfirm() {
        System.out.println("确认订单");
        Integer DStatus = Orders.DELIVERY_IN_PROGRESS;
        LocalDateTime orderTime = LocalDateTime.now().minusDays(1);
        orderMapper.updateByStatus(DStatus,Orders.CONFIRMED,null,null,LocalDateTime.now(),orderTime);
    }
}
