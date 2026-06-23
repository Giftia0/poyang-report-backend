package com.example.poyangreportbackend.mapper;

import com.example.poyangreportbackend.domain.AuthRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author asus
* @description 针对表【auth_record】的数据库操作Mapper
* @createDate 2024-03-15 15:44:27
* @Entity com.example.poyangreportbackend.domain.AuthRecord
*/
@Mapper
public interface AuthRecordMapper extends BaseMapper<AuthRecord> {

}




