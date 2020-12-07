package com.example.sens.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.entity.RechargeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author example
 */
@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {

    /**
     * 删除用户和预定关联
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    Integer deleteByUserId(Long userId);

    /**
     * 查询所有充值记录
     *
     * @return
     */
    List<RechargeRecord> findAll(@Param("startDate") String startDate,
                                 @Param("endDate") String endDate,
                                 Page page);


    /**
     * 获得该用户的充值记录
     *
     * @return
     */
    List<RechargeRecord> findByUserId(@Param("startDate") String startDate,
                                      @Param("endDate") String endDate,
                                      @Param("userId") Long userId, Page page);

    /**
     * 根据时间范围查询总金额
     *
     * @param startDate
     * @param endDate
     * @return
     */
    Integer getTotalMoneySum(@Param("startDate") String startDate,
                             @Param("endDate") String endDate);
}

