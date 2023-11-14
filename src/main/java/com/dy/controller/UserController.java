package com.dy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dy.domain.User;
import com.dy.entry.UserLogin;
import com.dy.entry.UserRegister;
import com.dy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.dy.utils.Constants.ADMIN_ROLE;
import static com.dy.utils.Constants.USER_LOGIN_STATUS;

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

    /**
     * 用户注册
     *
     * @param register
     * @return
     */
    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegister register) {
        //  controller 层也要有必要的校验
        if (register == null) {
            return null;
        }

        log.info("用户注册: {}", register);

        if (StringUtils.isAnyBlank(
                register.getUserAccount(),
                register.getUserPassword(),
                register.getCheckPassword())) {
            return null;
        }


        return userService.userRegister(register);
    }


    /**
     * 用户登录
     *
     * @param userLogin
     * @param request
     * @return
     */
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLogin userLogin, HttpServletRequest request) {

        //  controller 层也要有必要的校验
        if (userLogin == null) {
            return null;
        }

        log.info("用户登录~ {}" ,userLogin);

        if (StringUtils.isAnyBlank(
                userLogin.getUserAccount(),
                userLogin.getUserPassword())) {
            return null;
        }


        return userService.userLogin(userLogin.getUserAccount(), userLogin.getUserPassword(), request);
    }

    /**
     * 根据用户名模糊查询用户
     *
     * @param username
     * @return
     */
    @GetMapping("/search")
    public List<User> searchUserByName(String username, HttpServletRequest request) {

        //  仅管理员可以查询
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        log.info("根据用户名模糊查询用户: {}", username);

        return userService.searchUserByName(username, request);

    }



    /**
     * 根据用户 id 删除用户
     *
     * @param id
     * @return
     */
    @PutMapping("delete")
    public Boolean removeUserById(Long id, HttpServletRequest request) {
        //  仅管理员可以查询
        if (!isAdmin(request)) {
            return false;
        }

        log.info("根据 id 删除用户: {}", id);

        return userService.removeById(id);
    }


    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {
        log.info("获取当前用户: {}", request);
        return userService.getCurrentUser(request);
    }



    private static Boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);

        return user != null && Objects.equals(user.getUserRole(), ADMIN_ROLE);
    }

}
