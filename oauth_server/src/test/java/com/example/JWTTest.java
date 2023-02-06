package com.example;

import com.alibaba.fastjson2.JSONObject;
import com.example.consts.GlobalConsts;
import com.example.dto.OidcIdTokenBuilder;
import com.example.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.Map;

/**
 * Created by YangGuanRong
 * date: 2023/1/6
 */
@SpringBootTest
public class JWTTest {

    @Autowired
    RedisTemplate redisTemplate;

    private static final String service_key = "ifaas-hotel-robot-platform:";

    private static final String monitor_key = service_key + "monitor_now";

    /**
     * 创建token
     */
    @Test
    public void testCreateToken(){

        // 获取当前时间
        long now = System.currentTimeMillis();

        long exp = now + 60*1000;

        // 创建jwt构建对象
        JwtBuilder jwtBuilder = Jwts.builder();
        // 声明的标识 {"jti":"8888"}
        jwtBuilder.setId("8888")
                // 主体， 用户 {"sub":"Rose"}
                .setSubject("Rose")
                // 创建日期 {"iat":167297598}
                .setIssuedAt(new Date())
                // 加密算法和盐值
                .signWith(SignatureAlgorithm.HS256,"xxxx")
                //自定义声明
                .claim("role","admin")
                //过期时间
                .setExpiration(new Date(exp));

        String token = jwtBuilder.compact();
        System.out.println("token = " + token);

        System.out.println(" ======================================= ");

        String[] strings = token.split("\\.");

        System.out.println(Base64Codec.BASE64.decodeToString(strings[0]));
        System.out.println(Base64Codec.BASE64.decodeToString(strings[1]));
        //无法解密
        System.out.println(Base64Codec.BASE64.decodeToString(strings[2]));
    }

    /**
     * 解析
     */
    @Test
    public void testParseToken(){

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJpbnRlbGxpZi5jb20iLCJpYXQiOjE2NzU2NTg5MTEsImV4cCI6MTY3NTY1OTIxMCwic3ViIjoiMSIsIm5hbWUiOiJyb3NlIiwibG9naW5fbmFtZSI6InJvZGVMb2dpbiIsInBpY3R1cmUiOiJoYXNmZGhhc2ZkaCIsImF1ZCI6InN1YlBsYXQiLCJub25jZSI6IjEyM3NkIn0.kjZdlWtJ78mgjZ6wd2f9lWIe4HP59OftK-yZdibE1W8";

        Claims claims = Jwts.parser()
                .setSigningKey("xxxx")
                .parseClaimsJws(token)
                .getBody();

        String s = JSONObject.toJSONString(claims);
        System.out.println("s = " + s);
//        System.out.println("claims.getId() = " + claims.getId());
//
//        System.out.println("claims.getId() = " + claims.getSubject());
//
//        Object role = claims.get("role");
//        System.out.println("role = " + role);
//
//        System.out.println("签发时间 = " + claims.getIssuedAt());
//        System.out.println("过期时间 = " + claims.getExpiration());
//        System.out.println("当前时间 = " + new Date());

    }

    HashOperations<String,Integer,SysUser> hashOperations ;

    public void getHashOperation(){
        if (hashOperations == null){
            hashOperations = redisTemplate.opsForHash();
        }
    }

    @Test
    public void testPassword(){

        getHashOperation();

        for (int i = 1; i < 6; i++) {
            SysUser user = new SysUser();
            user.setId(i);
            user.setUsername("name"+i);
            hashOperations.put(monitor_key,i,user);
        }

        SysUser o = hashOperations.get(monitor_key, 2);
        System.out.println("o = " + o);

        o.setPassword("666");

        hashOperations.put(monitor_key,2,o);

        Map<Integer, SysUser> entries = hashOperations.entries(monitor_key);
        entries.forEach((k,v)->{
            System.out.println("k = " + k);
            System.out.println("v = " + v);
        });

    }

    @Test
    public void testIdToken(){
        long exp = System.currentTimeMillis() + 5*60*1000;
        String idToken = OidcIdTokenBuilder.builder()
                .signWith(SignatureAlgorithm.HS256,"xxxx")
                .setIssuer(GlobalConsts.ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(exp))
                .setSubject(String.valueOf(1))
                .setName("rose")
                .setLoginName("rodeLogin")
                .setPicture("http://baidu.pictrue.com/123.png")
                .setAudience("subPlat")
                .setNonce("123sd")
                .build();

        System.out.println("idToken = " + idToken);

        System.out.println(" ======================================= ");

        String[] strings = idToken.split("\\.");

        System.out.println(Base64Codec.BASE64.decodeToString(strings[0]));
        System.out.println(Base64Codec.BASE64.decodeToString(strings[1]));
        //无法解密
        System.out.println(Base64Codec.BASE64.decodeToString(strings[2]));
    }

}
