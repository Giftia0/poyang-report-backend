package com.example.poyangreportbackend.domain;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReportDTO {
    private Long id;
    private Long userId;
    private String title;
    private String detail;
    private String lng;
    private String lat;
    private String address;
    private String city;
    private String district;
    private String township;
    private String poi;
    private Integer isForbid;
    private Date createTime;
    private String type;
    private Integer category;
    private String status;
    private String reporter;
    private Long reporterId;

    private List<ReportImg> imgList;

    private ReportStatusDTO latestStatus;
}
