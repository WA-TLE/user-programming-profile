package com.dy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dy.common.BaseResponse;
import com.dy.domain.User;
import com.dy.entry.UserRegister;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    BaseResponse<Long> userRegister(UserRegister register);


    /**
     * 用户登录
     *
     * @param userAccount
     * @param password
     * @return
     */
    BaseResponse<User> userLogin(String userAccount, String password, HttpServletRequest request);


    /**
     * 根据用户名查询用户
     *
     * @param username
     * @param request
     * @return
     */
    BaseResponse<List<User>> searchUserByName(String username, HttpServletRequest request);

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    BaseResponse<User> getCurrentUser(HttpServletRequest request);


    /**
     * 用户注销功能
     *
     * @param request
     * @return
     */
    BaseResponse userLogout(HttpServletRequest request);


}
