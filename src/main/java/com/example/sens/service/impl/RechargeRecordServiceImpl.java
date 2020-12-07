package com.example.sens.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.entity.RechargeRecord;
import com.example.sens.mapper.RechargeRecordMapper;
import com.example.sens.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 言曌
 * @date 2020/3/22 3:19 下午
 */
@Service
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public BaseMapper<RechargeRecord> getRepository() {
        return rechargeRecordMapper;
    }

    @Override
    public QueryWrapper<RechargeRecord> getQueryWrapper(RechargeRecord rechargeRecord) {
        //对指定字段查询
        QueryWrapper<RechargeRecord> queryWrapper = new QueryWrapper<>();
        if (rechargeRecord != null) {
            if (rechargeRecord.getUserId() != null) {
                queryWrapper.eq("user_id", rechargeRecord.getUserId());
            }
        }
        return queryWrapper;
    }

    @Override
    public Page<RechargeRecord> findAll(String startDate, String endDate, Page<RechargeRecord> page) {
        return page.setRecords(rechargeRecordMapper.findAll(startDate, endDate, page));
    }

    @Override
    public Page<RechargeRecord> findByUserId(String startDate, String endDate, Long userId, Page<RechargeRecord> page) {
        return page.setRecords(rechargeRecordMapper.findByUserId(startDate, endDate, userId, page));
    }

    @Override
    public Integer getTotalMoneySum(String startDate, String endDate) {
        return rechargeRecordMapper.getTotalMoneySum(startDate, endDate);
    }
}
