package com.example.poyangreportbackend.mapper;

import com.example.poyangreportbackend.domain.ReportStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author asus
* @description 针对表【report_status】的数据库操作Mapper
* @createDate 2024-04-18 23:31:37
* @Entity com.example.poyangreportbackend.domain.ReportStatus
*/
@Mapper
public interface ReportStatusMapper extends BaseMapper<ReportStatus> {

}




