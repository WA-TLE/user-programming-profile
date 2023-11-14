package com.dy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dy.domain.User;
import com.dy.entry.UserRegister;

import javax.servlet.http.HttpServletRequest;

/**
* @author dy
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2023-11-12 21:02:54
*/
public interface UserService extends IService<User> {
    /**
     * 用户注册功能
     *
     * @param register@return
     */
    Long userRegister(UserRegister register);


    /**
     * 用户登录
     *
     * @param userAccount
     * @param password
     * @return
     */
    User userLogin(String userAccount, String password, HttpServletRequest request);


}
