package com.example.poyangreportbackend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class JWTUtil {
    private static final String SECRET = "<REDACTED_JWT_SECRET>";
    private static final Integer FAIL_TIME = 60 * 60 * 24 * 7;//7天

    public static String getToken(Map<String,String> map){
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, FAIL_TIME);

        JWTCreator.Builder builder = JWT.create();

        map.forEach((k,v)->{
            builder.withClaim(k,v);
        });

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        builder.withClaim("createTime",formatter.format(date) );

        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SECRET));

        return token;
    }

    public static DecodedJWT verifyToken(String token){
        return JWT.require(Algorithm.HMAC256(SECRET)).build().verify(token);
    }
}
