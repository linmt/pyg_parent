package cn.itcast.core.service;

import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;

public interface OrderService {

    public void add(Order order);

    public PayLog getPayLogByUserName(String userName);

    public void updatePayStatus(String userName);
}
