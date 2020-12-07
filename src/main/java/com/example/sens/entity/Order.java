package com.example.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sens.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 订单
 * @author 言曌
 * @date 2020/4/5 3:25 下午
 */
@Data
@TableName("t_order")
public class Order extends BaseEntity {

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 业主用户ID
     */
    private Long ownerUserId;

    /**
     * 房间ID
     */
    private Long postId;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 开始日期
     */
    private Date startDate;


    /**
     * 结束日期
     */
    private Date endDate;


    /**
     * 订单状态：0待支付，1已支付生效中，2已完结
     */
    private Integer status;

    /**
     * 总价
     */
    private Long price;


    /**
     * 房屋
     */
    @TableField(exist = false)
    private Post post;

    /**
     * 下单用户
     */
    @TableField(exist = false)
    private User user;

    /**
     * 业主用户
     */
    @TableField(exist = false)
    private User ownerUser;

}
