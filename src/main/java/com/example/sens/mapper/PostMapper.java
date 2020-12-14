package com.example.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.common.constant.CommonConstant;
import com.example.sens.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 根据条件查询房屋
     *
     * @param condition
     * @param page
     * @return
     */
    List<Post> findPostByCondition(@Param(CommonConstant.CONDITION) Post condition, Page page);

    /**
     * 根据租客用户ID查询
     *
     * @param userId
     * @param page
     * @return
     */
    List<Post> findByRentUserId(@Param("userId") Long userId, Page page);

    /**
     * 统计该分类的房屋
     *
     * @param cateId
     * @return
     */
    Integer countPostByCateId(Long cateId);


    /**
     * 获得最新房屋
     *
     * @param cityId
     * @param limit
     * @return
     */
    List<Post> getLatestPost(@Param("cityId") Long cityId, @Param("limit") Integer limit);

    /**
     * 根据状态统计
     *
     * @param postStatus 状态
     * @return
     */
    Integer countByStatus(Integer postStatus);

    /**
     * 获得合租房屋
     *
     * @return
     */
    List<Post> getUnionRentPost(Post post);

}

