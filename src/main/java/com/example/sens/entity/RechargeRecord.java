package com.example.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sens.common.base.BaseEntity;
import lombok.Data;

/**
 * @author 言曌
 * @date 2020/3/22 3:15 下午
 */
@TableName("recharge_record")
@Data
public class RechargeRecord extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 金额
     */
    private Long money;

    /**
     * 用户
     */
    @TableField(exist = false)
    private User user;
}
