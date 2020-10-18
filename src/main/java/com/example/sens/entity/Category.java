package com.example.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sens.common.base.BaseEntity;
import lombok.Data;

/**
 * <pre>
 *     房屋分类
 * </pre>
 *
 * @author : saysky
 * @date : 2019/11/30
 */
@Data
@TableName("category")
public class Category extends BaseEntity {

    /**
     * 分类名称
     */
    private String cateName;

    /**
     * 分类排序号
     */
    private Integer cateSort;

    /**
     * 分类描述
     */
    private String cateDesc;

    /**
     * 房屋数量
     */
    @TableField(exist = false)
    private Integer count;
}
