package com.example.sens.service;

import com.example.sens.entity.UserRoleRef;
import com.example.sens.common.base.BaseService;


public interface UserRoleRefService extends BaseService<UserRoleRef, Long> {

    /**
     * 根据用户Id删除
     *
     * @param userId 用户Id
     */
    void deleteByUserId(Long userId);


}
