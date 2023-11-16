package com.dy.controller;

import com.dy.common.BaseResponse;
import com.dy.common.ErrorCode;
import com.dy.common.Result;
import com.dy.domain.User;
import com.dy.entry.UserLogin;
import com.dy.entry.UserRegister;
import com.dy.exception.BusinessException;
import com.dy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    public BaseResponse<Long> userRegister(@RequestBody UserRegister register) {
        //  controller 层也要有必要的校验
        if (register == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        log.info("用户注册: {}", register);

        if (StringUtils.isAnyBlank(
                register.getUserAccount(),
                register.getUserPassword(),
                register.getCheckPassword(),
                register.getPlanetCode())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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
    public BaseResponse<User> userLogin(@RequestBody UserLogin userLogin, HttpServletRequest request) {

        //  controller 层也要有必要的校验
        if (userLogin == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        log.info("用户登录~ {}", userLogin);

        if (StringUtils.isAnyBlank(
                userLogin.getUserAccount(),
                userLogin.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        return userService.userLogin(userLogin.getUserAccount(), userLogin.getUserPassword(), request);
    }

    /**
     * 用户注销功能
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        log.info("用户注销: {}", request);

        return userService.userLogout(request);
    }

    /**
     * 根据用户名模糊查询用户
     *
     * @param username
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUserByName(String username, HttpServletRequest request) {

        //  仅管理员可以查询
        // TODO: 2023/11/16 这里的 username 永远为 null
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
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
    public BaseResponse<Boolean> removeUserById(Long id, HttpServletRequest request) {
        //  仅管理员可以查询
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        log.info("根据 id 删除用户: {}", id);
        boolean flag = userService.removeById(id);

        return Result.success(flag);
    }


    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        log.info("获取当前用户: {}", request);
        return userService.getCurrentUser(request);
    }


    private static Boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);

        return user != null && Objects.equals(user.getUserRole(), ADMIN_ROLE);
    }

}
