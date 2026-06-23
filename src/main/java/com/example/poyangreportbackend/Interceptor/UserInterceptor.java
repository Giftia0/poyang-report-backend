package com.example.poyangreportbackend.Interceptor;

import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.poyangreportbackend.common.Code;
import com.example.poyangreportbackend.common.Result;
import com.example.poyangreportbackend.service.user.UserService;
import com.example.poyangreportbackend.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
         System.out.println(request.getContextPath());
        //获取token
        String token = request.getHeader("Authorization");
        String msg;
        Integer code;
        try {
            DecodedJWT jwt = JWTUtil.verifyToken(token);//调用token解析的工具类进行解析
            String userId = jwt.getClaim("id").asString();
            request.setAttribute("userId",userId);
            return true;
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            msg = "登录状态过期，请重新登录";
            code = Code.TOKEN_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            msg = "未登录";
            code = Code.TOKEN_ERROR;
        }
        String jsonString = JSON.toJSONString(Result.error(msg, code));
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(jsonString);
        return false;  //异常不放行
    }

}