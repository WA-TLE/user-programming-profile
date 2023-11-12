package com.dy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dy.domain.User;
import com.dy.mapper.UserMapper;
import com.dy.service.UserService;
import org.springframework.stereotype.Service;

/**
* @author 微光
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2023-11-12 21:02:54
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

}




