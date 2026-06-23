package com.example.poyangreportbackend.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.poyangreportbackend.common.Result;
import com.example.poyangreportbackend.domain.AuthRecord;
import com.example.poyangreportbackend.domain.User;
import com.example.poyangreportbackend.service.authRecord.AuthRecordService;
import com.example.poyangreportbackend.service.user.UserService;
import com.example.poyangreportbackend.util.*;
import jakarta.servlet.http.HttpServletRequest;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthRecordService authRecordService;
    @Value("${imgPath}")
    private String imgPath;

    @GetMapping("/getUserInfo")
    public Result<User> getUserInfo(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        User user = userService.getById(userId);
        return Result.success("用户信息", user);
    }

    @GetMapping("/clearAuthRecord")
    public Result clearAuthRecord(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        LambdaQueryWrapper<AuthRecord> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AuthRecord::getUserId, userId);
        lqw.eq(AuthRecord::getIsOk, 0);
        List<AuthRecord> records = authRecordService.list(lqw);
        records.forEach(record -> {
            CosUtil.deleteImg(record.getImg());
        });
        authRecordService.remove(lqw);
        return Result.success("clear");
    }

    @GetMapping("/checkAuthRecord")
    public Result<String> checkAuthRecord(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        LambdaQueryWrapper<AuthRecord> lqwFace = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<AuthRecord> lqwBack = new LambdaQueryWrapper<>();
        lqwFace.eq(AuthRecord::getUserId, userId).eq(AuthRecord::getType, "face").orderByDesc(AuthRecord::getCreateTime);
        lqwBack.eq(AuthRecord::getUserId, userId).eq(AuthRecord::getType, "back").orderByDesc(AuthRecord::getCreateTime);
        AuthRecord faceRecord = authRecordService.getOne(lqwFace, false);
        AuthRecord backRecord = authRecordService.getOne(lqwBack, false);

        if (faceRecord == null || backRecord == null) return Result.error("认证失败");
        String info = faceRecord.getInfo();
        JSONObject infoObject = JSON.parseObject(info, JSONObject.class);
        String name = infoObject.getString("name");
        String num = infoObject.getString("num");

        boolean res = AuthUtil.verifyIdAndName(num, name);
        if (!res) return Result.error("认证失败");

        faceRecord.setIsOk(1);
        backRecord.setIsOk(1);
        authRecordService.updateById(faceRecord);
        authRecordService.updateById(backRecord);
        User user = new User();
        user.setId(Long.parseLong(userId));
        user.setIsAuth(1);
        userService.updateById(user);
        return Result.success("认证成功");
    }

    @PostMapping("/uploadIdcard")
    public Result<AuthRecord> uploadIdcard(HttpServletRequest request, MultipartFile multipartFile, String side) throws Exception {
        //原始图上传cos，识别后删除cos原图，处理图片上传cos，保存识别记录到数据库--->提交认证时根据记录校验
        File file = MultipartFileToFileUtils.multipartFileToFile(multipartFile);
        String url = CosUtil.uploadImg(file, "/id-card");
        if (file.exists()) file.delete();
        JSONObject jsonObject;
        List cardRegion;
        try {
            jsonObject = OcrUtil.idcardOcr(imgPath + url, side);

        } catch (Exception e) {
            CosUtil.deleteImg(url);
            String msg = "face".equals(side) ? "应上传人像面" : "应上传国徽面";
            return Result.error(msg);
        }
        List<Point> beforePoints = new ArrayList<>();
        cardRegion = (List) jsonObject.get("card_region");
        cardRegion.forEach(e -> {
            JSONObject obj = (JSONObject) e;
            Double x = obj.getDouble("x");
            Double y = obj.getDouble("y");
            Point point = new Point(x, y);
            beforePoints.add(point);
        });

        Mat mat = OpencvUtil.readImageFromUrl(imgPath + url);
        File afterFile = OpencvUtil.correctImg(mat, beforePoints);
        CosUtil.deleteImg(url);
        System.out.println(jsonObject);//-->
        //接收识别结果，删重复存数据库，返回识别结果和图像


        Boolean success = jsonObject.getBoolean("success");
        if (!success) return Result.error("识别失败，请重新上传");

        JSONObject warning = (JSONObject) jsonObject.get("warning");
        Integer isCopy = warning.getInteger("is_copy");
        Integer completenessScore = warning.getInteger("completeness_score");
        Double qualityScore = warning.getDouble("quality_score");
        Double tamperScore = warning.getDouble("tamper_score");

        if (isCopy == 1) return Result.error("请上传身份证原件照片");
        if (completenessScore < 95) return Result.error("上传的身份证照片不完整");
        if (tamperScore > 60) return Result.error("上传的身份证照片可能被篡改");
        if (qualityScore < 80) return Result.error("上传的身份证照片不符合要求");

        String distUrl = CosUtil.uploadImg(afterFile, "/id-card");
        AuthRecord authRecord = new AuthRecord();
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        authRecord.setUserId(userId);
        authRecord.setImg(distUrl);
        authRecord.setType(side);
        JSONObject info = new JSONObject();
        if ("face".equals(side)) {
            String name = jsonObject.getString("name");
            String num = jsonObject.getString("num");
            String nationality = jsonObject.getString("nationality");
            String sex = jsonObject.getString("sex");
            String birth = jsonObject.getString("birth");
            String address = jsonObject.getString("address");
            info.put("name", name);
            info.put("num", num);
            info.put("nationality", nationality);
            info.put("sex", sex);
            info.put("birth", birth);
            info.put("address", address);
        } else {
            String issue = jsonObject.getString("issue");
            String startDate = jsonObject.getString("start_date");
            String endDate = jsonObject.getString("end_date");
            info.put("issue", issue);
            info.put("start_date", startDate);
            info.put("end_date", endDate);
        }
        authRecord.setInfo(info.toJSONString());
        authRecordService.save(authRecord);
        return Result.success("上传成功", authRecord);
    }

    @GetMapping("/getAuthInfo")
    public Result<JSONObject> getAuthInfo(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        LambdaQueryWrapper<AuthRecord> lqwFace = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<AuthRecord> lqwBack = new LambdaQueryWrapper<>();
        lqwFace.eq(AuthRecord::getUserId, userId).eq(AuthRecord::getType, "face").eq(AuthRecord::getIsOk, 1);
        lqwBack.eq(AuthRecord::getUserId, userId).eq(AuthRecord::getType, "back").eq(AuthRecord::getIsOk, 1);
        AuthRecord faceRecord = authRecordService.getOne(lqwFace);
        AuthRecord backRecord = authRecordService.getOne(lqwBack);
        JSONObject faceInfo = JSON.parseObject(faceRecord.getInfo());
        JSONObject backInfo = JSON.parseObject(backRecord.getInfo());

        JSONObject res = new JSONObject();
        res.put("name", faceInfo.getString("name"));
        res.put("sex", faceInfo.getString("sex"));
        res.put("address", faceInfo.getString("address"));
        String num = faceInfo.getString("num");
        num = num.substring(0, 4) + "************" + num.substring(16);
        res.put("num", num);
        String date = backInfo.getString("end_date");
        date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);
        res.put("expiredDate", date);
        res.put("institution", backInfo.getString("issue"));

        return Result.success("success", res);
    }
}
