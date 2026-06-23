package com.example.poyangreportbackend.mapper;

import com.example.poyangreportbackend.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author asus
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-03-14 22:03:14
* @Entity com.example.poyangreportbackend.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




