package com.example.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据条件查询订单
     *
     * @param condition
     * @return
     */
    List<Order> findAll(@Param("condition") Order condition, Page page);

    /**
     * 根据条件查询总金额
     *
     * @param condition
     * @return
     */
    Integer getTotalPriceSum(@Param("condition") Order condition);

    /**
     * 更新超时订单状态为超时
     *
     * @return
     */
    Integer updateOverDueOrder();

    /**
     * 根据房屋ID删除
     * @param postId
     * @return
     */
    Integer deleteByPostId(Long postId);

    /**
     * 获得到期的订单
     * @return
     */
    List<Order> findOverDueOrder();
}

