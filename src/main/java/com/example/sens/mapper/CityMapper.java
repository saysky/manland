package com.example.sens.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.sens.entity.City;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author liuyanzhao
 */
@Mapper
public interface CityMapper extends BaseMapper<City> {

    /**
     * 获取所有城市
     * @return
     */
    List<City> findAllWithCount();
}

