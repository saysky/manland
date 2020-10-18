package com.example.sens.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.entity.Order;
import com.example.sens.mapper.OrderMapper;
import com.example.sens.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 言曌
 * @date 2020/4/6 2:01 下午
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public BaseMapper<Order> getRepository() {
        return orderMapper;
    }

    @Override
    public QueryWrapper<Order> getQueryWrapper(Order order) {
        //对指定字段查询
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        if (order != null) {
            if (order.getUserId() != null) {
                queryWrapper.eq("user_id", order.getUserId());
            }
            if (order.getOwnerUserId() != null) {
                queryWrapper.eq("owner_user_id", order.getOwnerUserId());
            }
            if (order.getPostId() != null) {
                queryWrapper.eq("post_id", order.getPostId());
            }
            if (order.getStatus() != null) {
                queryWrapper.eq("status", order.getStatus());
            }
            if (order.getStartDate() != null) {
                queryWrapper.eq("start_date", order.getStartDate());
            }
            if (order.getEndDate() != null) {
                queryWrapper.eq("end_date", order.getEndDate());
            }
        }
        return queryWrapper;
    }

    @Override
    public Integer getTotalPriceSum(Order condition) {
        return orderMapper.getTotalPriceSum(condition);
    }

    @Override
    public Page<Order> findAll(Order condition, Page<Order> page) {
        return page.setRecords(orderMapper.findAll(condition, page));
    }
}
