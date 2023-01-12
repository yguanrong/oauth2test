package com.example;

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

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODg4Iiwic3ViIjoiUm9zZSIsImlhdCI6MTY3Mjk4NDYwOSwiZXhwIjoxNjcyOTg0NjY5fQ.bpLaYkjUWMnss5AFrHlBj5hX-_7T1_wD94gTFgqYJ8E";

        Claims claims = Jwts.parser()
                .setSigningKey("xxxx")
                .parseClaimsJws(token)
                .getBody();

        System.out.println("claims.getId() = " + claims.getId());

        System.out.println("claims.getId() = " + claims.getSubject());

        Object role = claims.get("role");
        System.out.println("role = " + role);

        System.out.println("签发时间 = " + claims.getIssuedAt());
        System.out.println("过期时间 = " + claims.getExpiration());
        System.out.println("当前时间 = " + new Date());

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
}
