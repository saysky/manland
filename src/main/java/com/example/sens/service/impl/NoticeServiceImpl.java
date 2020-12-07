package com.example.sens.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sens.entity.Notice;
import com.example.sens.mapper.NoticeMapper;
import com.example.sens.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <pre>
 *     城市业务服务实现类
 * </pre>
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;


    @Override
    public BaseMapper<Notice> getRepository() {
        return noticeMapper;
    }

    @Override
    public QueryWrapper<Notice> getQueryWrapper(Notice notice) {
        //对指定字段查询
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        return queryWrapper;
    }

}
