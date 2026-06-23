package com.example.poyangreportbackend.controller;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.poyangreportbackend.common.Reg;
import com.example.poyangreportbackend.common.Result;
import com.example.poyangreportbackend.domain.User;
import com.example.poyangreportbackend.service.user.UserService;
import com.example.poyangreportbackend.service.visitor.VisitorService;
import com.example.poyangreportbackend.util.JWTUtil;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/visitor")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;
    @Autowired
    private UserService userService;

    //获取验证码
    @GetMapping("/getCheckCode/{phone}")
    public Result getCheckCode(@PathVariable @Pattern(regexp = Reg.PHONE, message = "无效手机号") String phone) {
        visitorService.sendCode(phone);
        return Result.success("验证码已发送");
    }

    @PostMapping("/loginByCheckCode")
    public Result loginByCheckCode(@RequestBody JSONObject jsonObject) {
        String phone = jsonObject.get("phone").toString();
        String code = jsonObject.get("code").toString();
        //校验验证码
        if (!visitorService.verifyCode(phone, code))
            return Result.error("无效验证码");
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, phone);

        User user = userService.getOne(lqw);
        //该手机号未注册
        if (user == null) {
            //注册
            user = new User();
            user.setPhone(phone);
            user.setRole("user");
            user.setAvatar("/avatar/default-avatar.png");
            String randomName;
            do {
                randomName = RandomStringUtils.randomAlphanumeric(12);
                lqw.clear();
                lqw.eq(User::getUsername, randomName);
            } while (userService.count(lqw) != 0);
            user.setUsername(randomName);
            userService.save(user);
        }
        //登录
        Map map = new HashMap<>();
        map.put("id", user.getId().toString());
        String token = JWTUtil.getToken(map);
        return Result.success("登录成功", token);
    }
}
