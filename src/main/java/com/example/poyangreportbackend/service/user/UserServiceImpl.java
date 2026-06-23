package com.example.poyangreportbackend.service.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.poyangreportbackend.domain.User;
import com.example.poyangreportbackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * @author asus
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-03-14 22:03:14
 */
@Service
class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService{

}