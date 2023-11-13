package com.dy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dy.domain.User;

/**
* @author dy
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-11-12 21:02:54
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册功能
     *
     * @param userAccount
     * @param password
     * @param checkPassword
     * @return
     */
    Long userRegister(String userAccount, String password, String checkPassword);
}
