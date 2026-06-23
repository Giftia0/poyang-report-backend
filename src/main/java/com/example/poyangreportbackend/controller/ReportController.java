package com.example.poyangreportbackend.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.poyangreportbackend.common.Result;
import com.example.poyangreportbackend.domain.*;
import com.example.poyangreportbackend.mapper.ReportFormMapper;
import com.example.poyangreportbackend.service.reportForm.ReportFormService;
import com.example.poyangreportbackend.service.reportImg.ReportImgService;
import com.example.poyangreportbackend.service.reportStatus.ReportStatusService;
import com.example.poyangreportbackend.service.statusImg.StatusImgService;
import com.example.poyangreportbackend.service.user.UserService;
import com.example.poyangreportbackend.util.CosUtil;
import com.example.poyangreportbackend.util.MultipartFileToFileUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportFormService reportFormService;

    @Autowired
    private ReportFormMapper reportFormMapper;

    @Autowired
    private ReportImgService reportImgService;

    @Autowired
    private StatusImgService statusImgService;

    @Autowired
    private ReportStatusService reportStatusService;

    @Autowired
    private UserService userService;

    @PostMapping("/addReport")
    public Result<Long> addReport(HttpServletRequest request, @RequestBody ReportForm report) {
        String userId = request.getAttribute("userId").toString();
        report.setUserId(Long.valueOf(userId));
        report.setOperatorId(Long.valueOf(userId));
        boolean isSuccess = reportFormService.save(report);
        if (!isSuccess) return Result.error("提交失败,请重试");
        ReportStatus reportStatus = new ReportStatus();
        reportStatus.setStatus("dangerous");
        reportStatus.setAction("提交举报线索");
        reportStatus.setIdx(1);
        reportStatus.setReportId(report.getId());
        reportStatus.setOperatorId(Long.valueOf(userId));
        reportStatusService.save(reportStatus);
        return Result.success("提交成功", report.getId());
    }

    @PostMapping("/addReportImg")
    public Result addReportImg(MultipartFile multipartFile, Integer index, Long reportId) throws Exception {
        File file = MultipartFileToFileUtils.multipartFileToFile(multipartFile);
        String url = CosUtil.uploadImg(file, "/reportImg");
        ReportImg reportImg = new ReportImg();
        reportImg.setReportId(reportId);
        reportImg.setIdx(index);
        reportImg.setUrl(url);
        reportImgService.save(reportImg);
        return Result.success("成功");
    }

    @GetMapping("/getMyReport")
    public Result getMyReport(HttpServletRequest request, Integer category, Integer status, Integer time, Long lastVisit) {
        String userId = request.getAttribute("userId").toString();
        LambdaQueryWrapper<ReportForm> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ReportForm::getUserId, userId);
        lqw.eq(ReportForm::getCategory, category);

        if (status != null) {
            if (status == 1) lqw.eq(ReportForm::getStatus, "dangerous");
            if (status == 2) lqw.eq(ReportForm::getStatus, "follow");
            if (status == 3) lqw.in(ReportForm::getStatus, "success", "evaluate");
            if (status == 4) lqw.eq(ReportForm::getStatus, "end");
            if (status == 5) lqw.eq(ReportForm::getStatus, "evaluate");
        }

        if (time != null && time != 0) {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = null;
            LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999);
            if (time == 1) startOfDay = LocalDate.now().atStartOfDay();
            if (time == 2) startOfDay = today.minus(1, ChronoUnit.WEEKS);
            if (time == 3) startOfDay = today.minus(1, ChronoUnit.MONTHS);
            if (time == 4) startOfDay = today.minus(6, ChronoUnit.MONTHS);
            if (time == 5) startOfDay = today.minus(1, ChronoUnit.YEARS);
            lqw.between(ReportForm::getCreateTime, startOfDay, endOfDay);
        }

        lqw.orderByDesc(ReportForm::getCreateTime);
        List<ReportForm> list = reportFormService.list(lqw);
        List<ReportDTO> reportDTOList = new ArrayList<>();
        list.forEach(item -> {
            ReportDTO reportDTO = new ReportDTO();
            BeanUtils.copyProperties(item, reportDTO);
            ReportStatusDTO latestStatus = reportStatusService.getLatestStatus(item.getId());
            reportDTO.setLatestStatus(latestStatus);
            reportDTOList.add(reportDTO);
        });
        System.out.println(reportDTOList);
        return Result.success("成功", reportDTOList);
    }

    @GetMapping("/getToDoReport")
    public Result getToDoReport(HttpServletRequest request, Integer category, Integer status, Integer time, Long lastVisit) {
        String userId = request.getAttribute("userId").toString();
        LambdaQueryWrapper<ReportForm> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ReportForm::getCategory, category);
        if (category == 0)
            lqw.eq(ReportForm::getOperatorId, Long.valueOf(userId));

        if (status != null) {
            if (status == 1) lqw.eq(ReportForm::getStatus, "dangerous");
            if (status == 2) lqw.eq(ReportForm::getStatus, "follow");
            if (status == 3) lqw.in(ReportForm::getStatus, "success", "evaluate");
            if (status == 4) lqw.eq(ReportForm::getStatus, "end");
            if (status == 5) lqw.eq(ReportForm::getStatus, "evaluate");
        }

        if (time != null && time != 0) {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = null;
            LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999);
            if (time == 1) startOfDay = LocalDate.now().atStartOfDay();
            if (time == 2) startOfDay = today.minus(1, ChronoUnit.WEEKS);
            if (time == 3) startOfDay = today.minus(1, ChronoUnit.MONTHS);
            if (time == 4) startOfDay = today.minus(6, ChronoUnit.MONTHS);
            if (time == 5) startOfDay = today.minus(1, ChronoUnit.YEARS);
            lqw.between(ReportForm::getCreateTime, startOfDay, endOfDay);
        }

        lqw.orderByDesc(ReportForm::getCreateTime);
        List<ReportForm> list = reportFormService.list(lqw);
        List<ReportDTO> reportDTOList = new ArrayList<>();
        list.forEach(item -> {
            ReportDTO reportDTO = new ReportDTO();
            BeanUtils.copyProperties(item, reportDTO);
            ReportStatusDTO latestStatus = reportStatusService.getLatestStatus(item.getId());
            reportDTO.setLatestStatus(latestStatus);
            reportDTOList.add(reportDTO);
        });
        return Result.success("成功", reportDTOList);
    }

    @GetMapping("/getReportList")
    public Result getReportList(HttpServletRequest request, Integer category, Integer status, Integer time, Long lastVisit) {
        String userId = request.getAttribute("userId").toString();
        LambdaQueryWrapper<ReportForm> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ReportForm::getCategory, category);

        if (status != null) {
            if (status == 1) lqw.eq(ReportForm::getStatus, "dangerous");
            if (status == 2) lqw.eq(ReportForm::getStatus, "follow");
            if (status == 3) lqw.in(ReportForm::getStatus, "success", "evaluate");
            if (status == 4) lqw.eq(ReportForm::getStatus, "end");
            if (status == 5) lqw.eq(ReportForm::getStatus, "evaluate");
        }

        if (time != null && time != 0) {
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = null;
            LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999);
            if (time == 1) startOfDay = LocalDate.now().atStartOfDay();
            if (time == 2) startOfDay = today.minus(1, ChronoUnit.WEEKS);
            if (time == 3) startOfDay = today.minus(1, ChronoUnit.MONTHS);
            if (time == 4) startOfDay = today.minus(6, ChronoUnit.MONTHS);
            if (time == 5) startOfDay = today.minus(1, ChronoUnit.YEARS);
            lqw.between(ReportForm::getCreateTime, startOfDay, endOfDay);
        }

        lqw.orderByDesc(ReportForm::getCreateTime);
        List<ReportForm> list = reportFormService.list(lqw);
        List<ReportDTO> reportDTOList = new ArrayList<>();
        list.forEach(item -> {
            ReportDTO reportDTO = new ReportDTO();
            BeanUtils.copyProperties(item, reportDTO);
            ReportStatusDTO latestStatus = reportStatusService.getLatestStatus(item.getId());
            reportDTO.setLatestStatus(latestStatus);
            reportDTOList.add(reportDTO);
        });
        return Result.success("成功", reportDTOList);
    }

    @GetMapping("/getReportDetail")
    public Result getReportDetail(Long reportId) {
        //举报信息
        ReportForm report = reportFormService.getById(reportId);
        //图片材料
        LambdaQueryWrapper<ReportImg> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ReportImg::getReportId, reportId);
        lqw.orderByAsc(ReportImg::getIdx);
        List<ReportImg> reportImg = reportImgService.list(lqw);
        //最近状态
        ReportStatusDTO latestStatus = reportStatusService.getLatestStatus(reportId);
        //举报人
        User user = userService.getById(report.getUserId());

        ReportDTO reportDTO = new ReportDTO();
        BeanUtils.copyProperties(report, reportDTO);
        reportDTO.setReporterId(user.getId());
        reportDTO.setReporter(user.getUsername());
        reportDTO.setImgList(reportImg);
        reportDTO.setLatestStatus(latestStatus);

        return Result.success("成功", reportDTO);
    }

    @GetMapping("/getReportStatusList")
    public Result<List<ReportStatusDTO>> getReportStatusList(Long id) {
        LambdaQueryWrapper<ReportStatus> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ReportStatus::getReportId, id);
        List<ReportStatus> statusList = reportStatusService.list(lqw);

        List<ReportStatusDTO> list = new ArrayList<>();
        statusList.forEach(item -> {
            User user = userService.getById(item.getOperatorId());
            ReportStatusDTO statusDTO = new ReportStatusDTO();
            BeanUtils.copyProperties(item, statusDTO);
            statusDTO.setOperator(user.getUsername());
            LambdaQueryWrapper<StatusImg> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(StatusImg::getStatusId, item.getId());
            lambdaQueryWrapper.orderByAsc(StatusImg::getIdx);
            List<StatusImg> imgList = statusImgService.list(lambdaQueryWrapper);
            System.out.println(imgList);
            statusDTO.setImgList(imgList);
            list.add(statusDTO);
        });
        return Result.success("成功", list);
    }

    @GetMapping("/getLatestStatus")
    public Result<ReportStatusDTO> getLatestStatus(Long id) {
        ReportStatusDTO latestStatus = reportStatusService.getLatestStatus(id);
        return Result.success("成功", latestStatus);
    }

    @GetMapping("/addToDo")
    public Result addToDo(HttpServletRequest request, Long reportId) {
        String userId = request.getAttribute("userId").toString();

        ReportStatusDTO latestStatus = reportStatusService.getLatestStatus(reportId);
        if (!"dangerous".equals(latestStatus.getStatus())) return Result.error("异常操作");

        ReportForm reportForm = new ReportForm();
        reportForm.setId(reportId);
        reportForm.setStatus("follow");
        reportForm.setOperatorId(Long.valueOf(userId));
        reportFormService.updateById(reportForm);

        ReportStatus status = new ReportStatus();
        status.setReportId(reportId);
        status.setStatus("follow");
        status.setIdx(latestStatus.getIdx() + 1);
        status.setOperatorId(Long.valueOf(userId));
        status.setAction("该线索已被加入待办");
        reportStatusService.save(status);
        return Result.success("已加入待办");
    }

    @GetMapping("/removeToDo")
    public Result removeToDo(HttpServletRequest request, Long reportId, String content) {
        System.out.println(content);
        String userId = request.getAttribute("userId").toString();

        ReportStatusDTO latestStatus = reportStatusService.getLatestStatus(reportId);
        if (!"follow".equals(latestStatus.getStatus())) return Result.error("异常操作");

        ReportForm reportForm = new ReportForm();
        reportForm.setId(reportId);
        reportForm.setStatus("dangerous");
        LambdaUpdateWrapper<ReportForm> lqw = new LambdaUpdateWrapper<>();
        lqw.set(ReportForm::getOperatorId, null);
        reportFormService.update(reportForm, lqw);

        ReportStatus status = new ReportStatus();
        status.setReportId(reportId);
        status.setStatus("dangerous");
        status.setIdx(latestStatus.getIdx() + 1);
        status.setOperatorId(Long.valueOf(userId));
        status.setAction("该线索已被移出待办，原因：" + content);
        System.out.println(status.getAction());
        reportStatusService.save(status);
        return Result.success("已加入待办");
    }

    @PostMapping("/addStatus")
    public Result<Long> addStatus(HttpServletRequest request, @RequestBody ReportStatus reportStatus) {
        String userId = request.getAttribute("userId").toString();

        ReportStatusDTO latestStatus = reportStatusService.getLatestStatus(reportStatus.getReportId());
//        if (!"follow".equals(latestStatus.getStatus())) return Result.error("异常操作");

        ReportForm reportForm = new ReportForm();
        reportForm.setId(reportStatus.getReportId());
        reportForm.setStatus(reportStatus.getStatus());
        reportFormService.updateById(reportForm);

        reportStatus.setOperatorId(Long.valueOf(userId));
        reportStatus.setIdx(latestStatus.getIdx() + 1);
        reportStatus.setReportId(reportStatus.getReportId());
        reportStatus.setOperatorId(Long.valueOf(userId));

        boolean isSuccess = reportStatusService.save(reportStatus);
        if (!isSuccess) return Result.error("提交失败,请重试");

        return Result.success("提交成功", reportStatus.getId());
    }

    @PostMapping("/addStatusImg")
    public Result addStatusImg(MultipartFile multipartFile, Integer index, Long statusId) throws Exception {
        System.out.println(statusId);
        File file = MultipartFileToFileUtils.multipartFileToFile(multipartFile);
        String url = CosUtil.uploadImg(file, "/statusImg");
        StatusImg statusImg = new StatusImg();
        statusImg.setStatusId(statusId);
        statusImg.setIdx(index);
        statusImg.setUrl(url);
        statusImgService.save(statusImg);
        return Result.success("成功");
    }


    @PostMapping("/replyReport")
    public Result replyReport(HttpServletRequest request, Long id, String content) {
        String userId = request.getAttribute("userId").toString();

        ReportForm reportForm = new ReportForm();
        reportForm.setId(id);
        reportForm.setStatus("success");
        reportForm.setOperatorId(Long.valueOf(userId));
        reportFormService.updateById(reportForm);

        ReportStatus status = new ReportStatus();
        status.setStatus("success");
        status.setIdx(2);
        status.setReportId(id);
        status.setOperatorId(Long.valueOf(userId));
        status.setAction(content);
        reportStatusService.save(status);
        return Result.success("成功");
    }

    @GetMapping("/getReportDataView")
    public Result getReportDataView() {
        long total = 0;
        long historyTotal = 0;
        long inTimeCnt = 0;
        long evaluateTotal = 0;
        long satisfactionCnt = 0;
        long successCnt = 0;
        long dangerousCnt = 0;

        //统计及时率
        LambdaQueryWrapper<ReportStatus> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ReportStatus::getStatus, "success");
        List<ReportStatus> list = reportStatusService.list(lambdaQueryWrapper);
        list = list.stream().filter(item -> {
            Long reportId = item.getReportId();
            LambdaQueryWrapper<ReportForm> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ReportForm::getCategory, 0);
            lqw.eq(ReportForm::getId, reportId);
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.minus(1, ChronoUnit.MONTHS);
            LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999);
            lqw.between(ReportForm::getCreateTime, startOfDay, endOfDay);
            ReportForm report = reportFormService.getOne(lqw);
            return report != null;
        }).toList();
        successCnt = list.size();
        for (ReportStatus item : list) {
            Long reportId = item.getReportId();
            LambdaQueryWrapper<ReportStatus> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ReportStatus::getReportId, reportId);
            lqw.orderByAsc(ReportStatus::getIdx);
            lqw.last("LIMIT 1");
            ReportStatus begin = reportStatusService.getOne(lqw);

            // 计算两个Date之间的时间差（毫秒）
            long diffInMillis = item.getCreateTime().getTime() - begin.getCreateTime().getTime();

            // 判断时间差是否小于24小时
            if (diffInMillis <= 24 * 1000 * 60 * 60) {
                inTimeCnt++;
            }
        }

        //统计满意率
        lambdaQueryWrapper.clear();
        lambdaQueryWrapper.eq(ReportStatus::getStatus, "evaluate");
        list = reportStatusService.list(lambdaQueryWrapper);
        evaluateTotal = list.size();
        list = list.stream().filter(item -> {
            Long reportId = item.getReportId();
            LambdaQueryWrapper<ReportForm> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ReportForm::getCategory, 0);
            lqw.eq(ReportForm::getId, reportId);
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.minus(1, ChronoUnit.MONTHS);
            LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999);
            lqw.between(ReportForm::getCreateTime, startOfDay, endOfDay);
            ReportForm report = reportFormService.getOne(lqw);
            return report != null;
        }).toList();
        for (ReportStatus item : list) {
            Double rate = Double.valueOf(item.getAction());
            if (rate >= 3) satisfactionCnt++;
        }

        //统计历史总数
        LambdaQueryWrapper<ReportForm> reportFormLambdaQueryWrapper = new LambdaQueryWrapper<>();
        reportFormLambdaQueryWrapper.eq(ReportForm::getCategory, 0);
        historyTotal = reportFormService.count(reportFormLambdaQueryWrapper);

        //统计总数
        reportFormLambdaQueryWrapper.clear();
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startOfDay = today.minus(1, ChronoUnit.MONTHS);
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999);
        reportFormLambdaQueryWrapper.between(ReportForm::getCreateTime, startOfDay, endOfDay);
        reportFormLambdaQueryWrapper.eq(ReportForm::getCategory, 0);
        total = reportFormService.count(reportFormLambdaQueryWrapper);

        //统计dangerous
        reportFormLambdaQueryWrapper.clear();
        reportFormLambdaQueryWrapper.eq(ReportForm::getCategory, 0);
        reportFormLambdaQueryWrapper.eq(ReportForm::getStatus, "dangerous");
        reportFormLambdaQueryWrapper.between(ReportForm::getCreateTime, startOfDay, endOfDay);
        dangerousCnt = reportFormService.count(reportFormLambdaQueryWrapper);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", total);
        jsonObject.put("historyTotal", historyTotal);
        jsonObject.put("successCnt", successCnt);
        jsonObject.put("dangerousCnt", dangerousCnt);
        jsonObject.put("inTimeRatio", String.format("%.1f", 100.0 * inTimeCnt / successCnt));
        jsonObject.put("satisfactionRatio", String.format("%.1f", 100.0 * satisfactionCnt / evaluateTotal));
        return Result.success("成功", jsonObject);
    }


    @GetMapping("/getPieChartData")
    public Result getPieChartData(LocalDate start, LocalDate end) {
        String[] types = {"非法垂钓", "非法捕捞", "电鱼炸鱼", "水域异常", "其他"};
        List<JSONObject> list = new ArrayList<>();

        for (String type : types) {
            JSONObject jsonObject = new JSONObject();
            LambdaQueryWrapper<ReportForm> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ReportForm::getCategory, 0);
            lqw.eq(ReportForm::getType, type);
            lqw.between(ReportForm::getCreateTime, start, end.atTime(23, 59, 59));
            long count = reportFormService.count(lqw);
            jsonObject.put("name", type);
            jsonObject.put("value", (int) count);
            list.add(jsonObject);
        }

        return Result.success("成功", list);
    }

    @GetMapping("/getBarChartData")
    public Result getBarChartData(LocalDate start, LocalDate end, Integer viewMode) {
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        List<Map<String, Object>> list = null;
        if (viewMode == 0) {
            startDateTime = start.withDayOfYear(1).atStartOfDay();
            endDateTime = end.with(TemporalAdjusters.lastDayOfYear()).atTime(23, 59, 59);
            list = reportFormMapper.selectCountByYear(startDateTime, endDateTime);
        } else if (viewMode == 1) {
            startDateTime = start.withDayOfMonth(1).atStartOfDay();
            endDateTime = end.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
            list = reportFormMapper.selectCountByMonth(startDateTime, endDateTime);
        } else if (viewMode == 2) {
            startDateTime = start.atStartOfDay();
            endDateTime = end.atTime(23, 59, 59);
            list = reportFormMapper.selectCountByDay(startDateTime, endDateTime);
        }
        System.out.println(list);

        return Result.success("成功", list);
    }
}
