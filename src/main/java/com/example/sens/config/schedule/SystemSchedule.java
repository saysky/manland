package com.example.sens.config.schedule;

import com.example.sens.entity.Order;
import com.example.sens.entity.Post;
import com.example.sens.enums.OrderStatusEnum;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.mapper.OrderMapper;
import com.example.sens.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时器
 *
 * @author 言曌
 * @date 2020/3/21 7:18 下午
 */
@Component
public class SystemSchedule {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 更新到期的订单
     */
    @Scheduled(fixedRate = 10000)
    @Transactional(rollbackFor = Exception.class)
    public void updatePostStatus() {
        List<Order> orderList = orderMapper.findOverDueOrder();
        for (Order order : orderList) {
            // 结束订单
            order.setStatus(OrderStatusEnum.FINISHED.getCode());
            orderMapper.updateById(order);

            // 更新房屋状态
            Post post = postMapper.selectById(order.getPostId());
            post.setPostStatus(PostStatusEnum.ON_SALE.getCode());
            postMapper.updateById(post);
        }


    }

}
