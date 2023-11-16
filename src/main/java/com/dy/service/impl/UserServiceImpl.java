package com.dy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.common.BaseResponse;
import com.dy.common.ErrorCode;
import com.dy.common.Result;
import com.dy.domain.User;
import com.dy.entry.UserRegister;
import com.dy.exception.BusinessException;
import com.dy.mapper.UserMapper;
import com.dy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dy.utils.Constants.*;

/**
 * @author dy
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-11-12 21:02:54
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册功能
     *
     * @param register@return
     */
    public BaseResponse userRegister(UserRegister register) {
        String userAccount = register.getUserAccount();
        String password = register.getUserPassword();
        String checkPassword = register.getCheckPassword();
        String planetCode = register.getPlanetCode();

        //  1. 校验
        //  1.1 非空校验
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  1.2 账号长度校验
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //  1.3 密码长度校验
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  星球编号长度校验
        if (planetCode.length() > 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  1.4 账户不包含特殊字符
        //  1.4 账户不包含特殊字符
        //  匹配标点字符, 符号字符, 一个或多个空白字符
//        String VALID_PATTERN = "\\pP|\\pS|\\s+";
        /*
            Pattern.compile(validPattern): 将之前写的 validPattern 编译成一种特殊的查找规则, 让计算机可以理解它
            .matcher(userAccount): 要在 userAccount 中使用之前定义的查找规则
            matcher.find() 配备到对应规则后就返回 true
         */
        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  1.5 密码和校验密码相同
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  1.6 判断用户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  1.7 判断星球编号是否重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planet_code", planetCode);
        Long countPlanet = userMapper.selectCount(queryWrapper);
        if (countPlanet > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        //  2. 对用户密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        User user = User.builder()
                .userAccount(userAccount)
                .userPassword(encryptPassword)
                .planetCode(planetCode)
                .build();


        int isSuccess = userMapper.insert(user);

        if (isSuccess <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }


        return Result.success();
    }


    /**
     * 用户登录
     *
     * @param userAccount
     * @param password
     * @return
     */
    public BaseResponse<User> userLogin(String userAccount, String password, HttpServletRequest request) {
        //  1. 校验
        //  1.1 非空校验
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  1.2 账号长度校验
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //  1.3 密码长度校验
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  1.4 用户名特殊字符校验
        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  2.1 判断密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_password", encryptPassword);
        userQueryWrapper.eq("user_account", userAccount);

        //  2.2 从数据库中查询用户
        User originUser = userMapper.selectOne(userQueryWrapper);

        if (originUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //  3. 用户信息脱敏
        User secureUser = getSecureUser(originUser);

        //  4. 记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATUS, secureUser);

        //  5. 返回脱敏后的用户信息
        return Result.success(secureUser);
    }


    /**
     * 根据用户名查询用户
     *
     * @param username
     * @param request
     * @return
     */
    public BaseResponse<List<User>> searchUserByName(String username, HttpServletRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();


        //  如果这里没有传入参数呢???
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("user_name", username);
        }

        List<User> userList = this.list(queryWrapper);
        ArrayList<User> secureUserList = new ArrayList<>(userList.size());

        for (User user : userList) {
            User secureUser = getSecureUser(user);
            secureUserList.add(secureUser);
        }

        return Result.success(secureUserList);


    }

    @Override
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        //  从 session 中获取当前登录用户的信息
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);

        //  如果当前用户未登录呢?
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        //  根据用户 id, 从数据中查询最新信息
        Long userId = user.getId();

        User currentUser = userMapper.selectById(userId);

        //  用户信息脱敏 返回
        User secureUser = getSecureUser(currentUser);
        return Result.success(secureUser);
    }

    /**
     * 用户注销功能
     *
     * @param request
     * @return
     */
    public BaseResponse userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATUS);

        return Result.success(1L);
    }

    /**
     * 用户信息脱敏
     *
     * @param originUser
     * @return
     */
    private static User getSecureUser(User originUser) {
        //  如果这里传进来的用户为 null 呢??
        if (originUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        User secureUser = new User();
        secureUser.setId(originUser.getId());
        secureUser.setUsername(originUser.getUsername());
        secureUser.setUserAccount(originUser.getUserAccount());
        secureUser.setAvatarUrl(originUser.getAvatarUrl());
        secureUser.setGender(originUser.getGender());
        secureUser.setEmail(originUser.getEmail());
        secureUser.setPhone(originUser.getPhone());
        secureUser.setUserRole(originUser.getUserRole());    //  把用户的权限也返回给前端
        secureUser.setUserStatus(originUser.getUserStatus());
        secureUser.setCreateTime(originUser.getCreateTime());
        secureUser.setPlanetCode(originUser.getPlanetCode());
        return secureUser;
    }


}




