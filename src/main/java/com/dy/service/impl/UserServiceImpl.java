package com.dy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.domain.User;
import com.dy.mapper.UserMapper;
import com.dy.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dy
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-11-12 21:02:54
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册功能
     *
     * @param userAccount
     * @param password
     * @param checkPassword
     * @return
     */
    public Long userRegister(String userAccount, String password, String checkPassword) {

        //  1. 校验
        //  1.1 非空校验
        if (StringUtils.isAnyBlank(userAccount, password, checkPassword)) {
            return -1L;
        }

        //  1.2 账号长度校验
        if (userAccount.length() < 4) {
            return -2L;
        }
        //  1.3 密码长度校验
        if (password.length() < 8) {
            return -3L;
        }

        //  1.4 账户不包含特殊字符
        //  1.4 账户不包含特殊字符
        //  匹配标点字符, 符号字符, 一个或多个空白字符
        String validPattern = "\\pP|\\pS|\\s+";
        /*
            Pattern.compile(validPattern): 将之前写的 validPattern 编译成一种特殊的查找规则, 让计算机可以理解它
            .matcher(userAccount): 要在 userAccount 中使用之前定义的查找规则
            matcher.find() 配备到对应规则后就返回 true
         */
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -4L;
        }

        //  1.5 密码和校验密码相同
        if (!password.equals(checkPassword)) {
            return -5L;
        }

        //  1.6 判断用户名是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return -6L;
        }

        //  2. 对用户密码进行加密
        final String SALT = "WA_TLE";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        User user = User.builder()
                .userAccount(userAccount)
                .userPassword(encryptPassword)
                .build();


        int isSuccess = userMapper.insert(user);

        if (isSuccess <= 0) {
            return -7L;
        }


        return 1L;
    }
}




