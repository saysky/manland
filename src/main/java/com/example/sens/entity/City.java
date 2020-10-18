package com.example.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sens.common.base.BaseEntity;
import lombok.Data;

/**
 * <pre>
 *     城市
 * </pre>
 *
 * @author : saysky
 * @date : 2019/11/30
 */
@Data
@TableName("city")
public class City extends BaseEntity {

    /**
     * 分类名称
     */
    private String cityName;

    /**
     * 房屋数
     */
    @TableField(exist = false)
    private Integer count;
}
