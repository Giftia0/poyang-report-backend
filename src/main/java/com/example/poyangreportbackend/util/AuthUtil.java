package com.example.poyangreportbackend.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class AuthUtil {
    static String host = "https://swidverify.market.alicloudapi.com";
    static String path = "/verify/identity";
    static String method = "POST";
    static String appcode = "21fee1992bcb4943bf9df67e7a23fd0b";

    public static boolean verifyIdAndName(String id, String name) {
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("name", name);
        bodys.put("id_number", id);

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            //获取response的body
            JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(response.getEntity()));

            String code = jsonObject.getString("result_code");
            if (!"0000".equals(code)) return false;

            JSONObject result = jsonObject.getJSONObject("result");
            Integer checkResult = result.getInteger("checkresult");
            if (checkResult != 1) return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
