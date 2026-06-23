package com.example.poyangreportbackend.service.reportStatus;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.poyangreportbackend.domain.ReportStatus;
import com.example.poyangreportbackend.domain.ReportStatusDTO;
import com.example.poyangreportbackend.domain.User;
import com.example.poyangreportbackend.mapper.ReportStatusMapper;
import com.example.poyangreportbackend.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author asus
* @description 针对表【report_status】的数据库操作Service实现
* @createDate 2024-04-18 22:36:54
*/
@Service
public class ReportStatusServiceImpl extends ServiceImpl<ReportStatusMapper, ReportStatus>
    implements ReportStatusService{

    @Autowired
    private ReportStatusMapper reportStatusMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ReportStatusDTO getLatestStatus(Long reportId) {
        LambdaQueryWrapper<ReportStatus> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ReportStatus::getReportId, reportId);
        lqw.orderByDesc(ReportStatus::getIdx);
        lqw.last("LIMIT 1");
        System.out.println(reportId);
        ReportStatus reportStatus = reportStatusMapper.selectOne(lqw);
        ReportStatusDTO reportStatusDTO = new ReportStatusDTO();
        BeanUtils.copyProperties(reportStatus,reportStatusDTO);

        Long operatorId = reportStatus.getOperatorId();
        User user = userMapper.selectById(operatorId);
        reportStatusDTO.setOperator(user.getUsername());
        return reportStatusDTO;
    }
}




