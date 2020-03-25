package com.example.sessionshare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpSession;
import java.util.Date;

@SpringBootTest
class SessionshareApplicationTests {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
    }


    @Test
    public void testRedis(){
        String key = "rxx_test0325";
        String value = "测试";
        //插入缓存
        redisTemplate.opsForValue().set(key, value);

        //取缓存
        System.out.println(redisTemplate.opsForValue().get("rxx_test0325"));
    }

    @Test
    public void testShare(HttpSession session){
        session.setAttribute("test", new Date());
        session.getAttribute("test");
    }

}
