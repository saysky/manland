package com.example.sens.config.schedule;

import com.example.sens.entity.Order;
import com.example.sens.entity.Post;
import com.example.sens.entity.User;
import com.example.sens.enums.OrderStatusEnum;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.mapper.OrderMapper;
import com.example.sens.mapper.PostMapper;
import com.example.sens.mapper.UserMapper;
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

    @Autowired
    private UserMapper userMapper;

    /**
     * 更新到期的订单
     */
    @Scheduled(fixedRate = 10000)
    @Transactional(rollbackFor = Exception.class)
    public void updatePostStatus() {
        List<Order> orderList = orderMapper.findOverDueOrder();
        for (Order order : orderList) {


            // 更新房屋状态
            Post post = postMapper.selectById(order.getPostId());
            if (post == null) {
                return;
            }

            // 退还押金
            User user = userMapper.selectById(order.getUserId());
            if (user == null) {
                return;
            }


            User ownerUser = userMapper.selectById(post.getId());
            if (ownerUser == null) {
                return;
            }

            if (ownerUser.getMoney() < post.getDeposit()) {
                // 业主余额不足，无法退回押金
                // 结束订单
                order.setStatus(OrderStatusEnum.DEPOSIT_RETURN_FAIL.getCode());
                orderMapper.updateById(order);
                return;
            }


            //  以下代码暂不考虑并发问题，暂不用乐观锁实现
            // 业主余额减少
            ownerUser.setMoney(ownerUser.getMoney() - post.getDeposit());
            userMapper.updateById(ownerUser);

            // 租客余额增加
            user.setMoney(user.getMoney() + post.getDeposit());
            userMapper.updateById(user);

            // 结束订单
            order.setStatus(OrderStatusEnum.FINISHED.getCode());
            orderMapper.updateById(order);

            post.setPostStatus(PostStatusEnum.ON_SALE.getCode());
            postMapper.updateById(post);

        }


    }

}
