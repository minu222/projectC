package com.lms.adminpages.pay.service;

import com.lms.adminpages.pay.dao.OrderDao;
import com.lms.adminpages.pay.entity.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderDao orderDao;


    @Autowired
    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public List<Order> getOrders(String keyword, String status) {
        return orderDao.searchOrders(keyword, status);
    }
}