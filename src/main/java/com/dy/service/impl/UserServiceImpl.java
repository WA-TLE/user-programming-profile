package com.dy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.domain.User;
import com.dy.entry.UserRegister;
import com.dy.mapper.UserMapper;
import com.dy.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dy.utils.Constants.SALT;
import static com.dy.utils.Constants.VALID_PATTERN;

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
    public Long userRegister(UserRegister register) {
        String userAccount = register.getUserAccount();
        String password = register.getUserPassword();
        String checkPassword = register.getCheckPassword();

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
//        String VALID_PATTERN = "\\pP|\\pS|\\s+";
        /*
            Pattern.compile(validPattern): 将之前写的 validPattern 编译成一种特殊的查找规则, 让计算机可以理解它
            .matcher(userAccount): 要在 userAccount 中使用之前定义的查找规则
            matcher.find() 配备到对应规则后就返回 true
         */
        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
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


    /**
     * 用户登录
     *
     * @param userAccount
     * @param password
     * @return
     */
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        //  1. 校验
        //  1.1 非空校验
        if (StringUtils.isAnyBlank(userAccount, password)) {
            return null;
        }

        //  1.2 账号长度校验
        if (userAccount.length() < 4) {
            return null;
        }
        //  1.3 密码长度校验
        if (password.length() < 8) {
            return null;
        }

        //  1.4 用户名特殊字符校验
        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }

        //  2.1 判断密码是否正确
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_password", encryptPassword);
        userQueryWrapper.eq("user_account", userAccount);

        //  2.2 从数据库中查询用户
        User originUser = userMapper.selectOne(userQueryWrapper);

        if (originUser == null) {
            return null;
        }

        //  3. 用户信息脱敏
        User secureUser = new User();
        secureUser.setId(originUser.getId());
        secureUser.setUserName(originUser.getUserName());
        secureUser.setUserAccount(originUser.getUserAccount());
        secureUser.setAvatarUrl(originUser.getAvatarUrl());
        secureUser.setGender(originUser.getGender());
        secureUser.setEmail(originUser.getEmail());
        secureUser.setPhone(originUser.getPhone());
        secureUser.setUserRole(originUser.getUserRole());    //  把用户的权限也返回给前端
        secureUser.setUserStatus(originUser.getUserStatus());
        secureUser.setCreateTime(originUser.getCreateTime());

        //  4. 记录用户登录状态
        request.getSession().setAttribute("userStatus", secureUser);

        //  5. 返回脱敏后的用户信息
        return secureUser;
    }


}




