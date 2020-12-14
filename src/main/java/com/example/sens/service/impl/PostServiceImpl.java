package com.example.sens.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.entity.*;
import com.example.sens.mapper.*;
import com.example.sens.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * <pre>
 *     房屋业务逻辑实现类
 * </pre>
 */
@Service
@Slf4j
public class PostServiceImpl implements PostService {


    @Autowired
    private PostMapper postMapper;

    @Override
    public Page<Post> findPostByCondition(Post condition, Page<Post> page) {
        List<Post> postList = postMapper.findPostByCondition(condition, page);
        return page.setRecords(postList);
    }

    @Override
    public Page<Post> findByRentUserId(Long userId, Page<Post> page) {
        List<Post> postList = postMapper.findByRentUserId(userId, page);
        return page.setRecords(postList);
    }


    @Override
    public BaseMapper<Post> getRepository() {
        return postMapper;
    }

    @Override
    public Post insert(Post post) {
        postMapper.insert(post);
        return post;
    }

    @Override
    public Post update(Post post) {
        postMapper.updateById(post);
        return post;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long postId) {
        postMapper.deleteById(postId);
    }

    @Override
    public QueryWrapper<Post> getQueryWrapper(Post post) {
        //对指定字段查询
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (post != null) {
            if (StrUtil.isNotBlank(post.getPostTitle())) {
                queryWrapper.like("post_title", post.getPostTitle());
            }
            if (StrUtil.isNotBlank(post.getPostContent())) {
                queryWrapper.like("post_content", post.getPostContent());
            }
            if (post.getPostStatus() != null && post.getPostStatus() != -1) {
                queryWrapper.eq("post_status", post.getPostStatus());
            }
        }
        return queryWrapper;
    }

    @Override
    public Post insertOrUpdate(Post post) {
        if (post.getId() == null) {
            insert(post);
        } else {
            update(post);
        }
        return post;
    }


    @Override
    public List<Post> getLatestPost(Long cityId, int limit) {
        return postMapper.getLatestPost(cityId, limit);
    }

    @Override
    public Integer countByStatus(Integer postStatus) {
        return postMapper.countByStatus(postStatus);
    }

    @Override
    public List<Post> getUnionRentPost(Post post) {

        Post temp = new Post();
        temp.setNumber(post.getNumber());
        temp.setUserId(post.getUserId());
        temp.setPostTitle(post.getPostTitle());
        temp.setCityId(post.getCityId());
        if (temp.getNumber() != null && temp.getNumber().length() > 2) {
            if (temp.getNumber().indexOf("室") != -1) {
                temp.setNumber(temp.getNumber().substring(0, temp.getNumber().indexOf("室") + 1));
            }
        }
        return postMapper.getUnionRentPost(temp);
    }

}

