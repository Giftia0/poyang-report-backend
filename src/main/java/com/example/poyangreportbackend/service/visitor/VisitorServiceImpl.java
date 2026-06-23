package com.example.poyangreportbackend.service.visitor;

import com.example.poyangreportbackend.util.UniSmsUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VisitorServiceImpl implements VisitorService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean sendCode(String phone) {
        String code = RandomStringUtils.randomNumeric(6);
        redisTemplate.opsForValue().set(phone, code, 600, TimeUnit.SECONDS);
        UniSmsUtil.sendCheckCode(phone, code);
        return true;
    }

    @Override
    public boolean verifyCode(String phone, String code) {
        Object object = redisTemplate.opsForValue().get(phone);
        if (object == null || !code.equals(object.toString())) {
            return false;
        }
        redisTemplate.delete(phone);
        return true;
    }
}
