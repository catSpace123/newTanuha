package com.tanhua.server.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${tanhua.secret}")
    private String secret;

    /**
     * 生成JWT
     *  A.B.C    (A，一般存加密规则之类的，B存电话号码，用户id，C一般是AB加密后得到 用于二次校验，)
     *  eyJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiIxNTk4ODQ1Nzg1NSIsImlkIjoxMDAxOSwiaWF0IjoxNjIyNzE1Nzc1fQ.ekytGL4mZWsgNgJLwFW-73LM74514airW4UQC4OprOY
     *
     * @return
     */
    public String createJWT(String phone,Long userId) {
        Map<String, Object> claims = new HashMap<String, Object>();
        //将电话号码 用户id存入到map中
        claims.put("mobile", phone);
        claims.put("id", userId);
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secret);
        return builder.compact();
    }
}