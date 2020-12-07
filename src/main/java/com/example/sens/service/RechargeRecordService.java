package com.example.sens.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.common.base.BaseService;
import com.example.sens.entity.RechargeRecord;

/**
 * @author 言曌
 * @date 2020/3/22 3:18 下午
 */

public interface RechargeRecordService extends BaseService<RechargeRecord, Long> {


    Page<RechargeRecord> findAll(String startDate, String endDate, Page<RechargeRecord> page);

    /**
     * 根据用户ID获得预定列表
     *
     * @param userId
     * @param page
     * @return
     */
    Page<RechargeRecord> findByUserId(String startDate, String endDate, Long userId, Page<RechargeRecord> page);


    /**
     * 根据时间范围查询总金额
     *
     * @param startDate
     * @param endDate
     * @return
     */
    Integer getTotalMoneySum(String startDate, String endDate);
}
