package com.example.poyangreportbackend.mapper;

import com.example.poyangreportbackend.domain.ReportForm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
* @author asus
* @description 针对表【report_form】的数据库操作Mapper
* @createDate 2024-04-26 00:50:18
* @Entity com.example.poyangreportbackend.domain.ReportForm
*/
@Mapper
public interface ReportFormMapper extends BaseMapper<ReportForm> {

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as date, COUNT(*) as value " +
            "FROM report_form " +
            "WHERE category = 0 " +
            "AND create_time BETWEEN #{start} AND #{end} " +
            "GROUP BY date ")
    List<Map<String, Object>> selectCountByMonth(LocalDateTime start, LocalDateTime end);

    @Select("SELECT DATE_FORMAT(create_time, '%Y') as date, COUNT(*) as value " +
            "FROM report_form " +
            "WHERE category = 0 " +
            "AND create_time BETWEEN #{start} AND #{end} " +
            "GROUP BY date ")
    List<Map<String, Object>> selectCountByYear(LocalDateTime start, LocalDateTime end);

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, COUNT(*) as value " +
            "FROM report_form " +
            "WHERE category = 0 " +
            "AND create_time BETWEEN #{start} AND #{end} " +
            "GROUP BY date ")
    List<Map<String, Object>> selectCountByDay(LocalDateTime start, LocalDateTime end);
}




