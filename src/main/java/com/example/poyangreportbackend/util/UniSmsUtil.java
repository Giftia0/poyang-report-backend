package com.example.poyangreportbackend.util;

import com.apistd.uni.Uni;
import com.apistd.uni.UniException;
import com.apistd.uni.UniResponse;
import com.apistd.uni.sms.UniMessage;
import com.apistd.uni.sms.UniSMS;

import java.util.HashMap;
import java.util.Map;

public class UniSmsUtil {
    public static void sendCheckCode(String phone,String code){
        Uni.init("NJoCmBTVKYjTuaK6pLYWC7zPaFgsVGeepwYFgACLvTjAiRw1o"); // 若使用简易验签模式仅传入第一个参数即可

        // 设置自定义参数 (变量短信)
        Map<String, String> templateData = new HashMap<String, String>();
        templateData.put("code", code);
        templateData.put("ttl","10");

        // 构建信息
        UniMessage message = UniSMS.buildMessage()
                .setTo(phone)
                .setSignature("胥佳乐测试")
                .setTemplateId("pub_verif_ttl")
                .setTemplateData(templateData);

        // 发送短信
        try {
            UniResponse res = message.send();
            System.out.println(res);
        } catch (UniException e) {
            System.out.println("Error: " + e);
            System.out.println("RequestId: " + e.requestId);
        }
    }
}
