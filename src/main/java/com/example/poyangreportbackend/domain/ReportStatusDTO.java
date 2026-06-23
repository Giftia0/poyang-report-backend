package com.example.poyangreportbackend.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReportStatusDTO {

    private Long id;
    private Long reportId;
    private Integer idx;
    private String status;
    private String action;
    private Long operatorId;
    private String operator;
    private Date createTime;
    private List<StatusImg> imgList;
}
