package com.example.poyangreportbackend.service.reportStatus;

import com.example.poyangreportbackend.domain.ReportStatus;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.poyangreportbackend.domain.ReportStatusDTO;

/**
* @author asus
* @description 针对表【report_status】的数据库操作Service
* @createDate 2024-04-18 22:36:54
*/
public interface ReportStatusService extends IService<ReportStatus> {
    public ReportStatusDTO getLatestStatus(Long reportId);
}
