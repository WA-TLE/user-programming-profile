package com.dy.controller;

import com.dy.domain.User;
import com.dy.entry.UserLogin;
import com.dy.entry.UserRegister;
import com.dy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: dy
 * @Date: 2023/11/13 21:49
 * @Description:
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegister register) {
        //  controller 层也要有必要的校验
        if (register == null) {
            return null;
        }
        if (StringUtils.isAnyBlank(
                register.getUserAccount(),
                register.getUserPassword(),
                register.getCheckPassword())) {
            return null;
        }


        return userService.userRegister(register);
    }


    @PostMapping("/login")
    public User userLogin(@RequestBody UserLogin userLogin, HttpServletRequest request) {

        //  controller 层也要有必要的校验
        if (userLogin == null) {
            return null;
        }
        if (StringUtils.isAnyBlank(
                userLogin.getUserAccount(),
                userLogin.getUserPassword())) {
            return null;
        }


        return userService.userLogin(userLogin.getUserAccount(), userLogin.getUserPassword(), request);
    }


}
