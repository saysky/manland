package com.example.sens.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sens.common.base.BaseEntity;
import lombok.Data;


/**
 * <pre>
 *     新闻公告
 * </pre>
 */
@Data
@TableName("notice")
public class Notice extends BaseEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;

}
