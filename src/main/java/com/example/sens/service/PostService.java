package com.example.sens.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.common.base.BaseService;
import com.example.sens.entity.Post;

import java.util.List;

/**
 * <pre>
 *     记录/页面业务逻辑接口
 * </pre>
 */
public interface PostService extends BaseService<Post, Long> {

    /**
     * 根据条件获得列表
     *
     * @param condition
     * @return
     */
    Page<Post> findPostByCondition(Post condition, Page<Post> page);


    /**
     * 获得最新房屋
     *
     * @param limit
     * @return
     */
    List<Post> getLatestPost(int limit);

    /**
     * 根据状态统计
     *
     * @param postStatus 状态
     * @return
     */
    Integer countByStatus(Integer postStatus);


}
