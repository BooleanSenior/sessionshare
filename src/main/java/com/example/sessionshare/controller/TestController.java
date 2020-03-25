package com.example.sessionshare.controller;

import com.example.sessionshare.pojo.Student;
import com.example.sessionshare.util.CookieUtil;
import com.example.sessionshare.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    /** token 前缀. */
    String TOKEN_PREFIX = "token.%s.%s";


    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping("/")
    String home(HttpSession session) {
        session.setAttribute("test778787877878", new Date());
        //取缓存
        System.out.println(redisTemplate.opsForValue().get("rxx_test0325"));
        //取缓存
        System.out.println(redisTemplate.opsForValue().get("token.rxx.12b65d16-120b-43da-8fc6-602776ed39ec"));
        return "Hello World!";
    }


    @RequestMapping("/login")
    public String login(@RequestParam("openid") String openid,
                              HttpServletResponse httpServletResponse,
                              Map<String,Object> map, HttpSession session){
        // 数据库匹配
        Student student  = new Student();     //通过关键字 查询用户 模拟用户
        student.setAge("23");

        if (student == null){   // 判断用户是否存在
            return "用户不存在";
        }

        String token = session.getId();   //获取登录 token
        Integer expire = 60;  //设置时间
        //设置 token 至 redis
        redisTemplate.opsForValue().set(
                String.format(TOKEN_PREFIX,"rxx",token) // key
                ,openid  //关键字  value
                ,expire,  // 时间
                TimeUnit.SECONDS);  //格式

        //设置 token 至 cookie
        CookieUtil.writeLoginToken(httpServletResponse,token);
        return "登陆成功！";
    }

    /**
     *
     * @param request
     * @param response
     * @param map
     * @param session
     * @return
     */
    @RequestMapping("/loginGet")
    public String loginGet(HttpServletRequest request, HttpServletResponse response,
                           Map<String,Object> map, HttpSession session){
        //1：取出Cookie
        Cookie[] cookies = request.getCookies();
        if(null != cookies && cookies.length > 0){
            for (Cookie cookie : cookies) {
                //2: 判断COokie中是否有CSESSIONID
                if("COOKIE_NAME".equals(cookie.getName())){
                    //3:有  直接使用
                    //取缓存
                    System.out.println(redisTemplate.opsForValue().get("token.rxx.12b65d16-120b-43da-8fc6-602776ed39ec"));
                    String s1 = redisTemplate.opsForValue().get("token.rxx.12b65d16-120b-43da-8fc6-602776ed39ec");
                    String s2 = redisTemplate.opsForValue().get(String.format(TOKEN_PREFIX,"rxx",cookie.getValue()));
                  //  String s3 =  redisUtils.getKey(String.format(TOKEN_PREFIX,"rxx",cookie.getValue()));
                    return cookie.getValue()+"---"+s1+"==="+s2+"***";
                }
            }
        }
        return "there is empty!";
    }


    @RequestMapping("/loginOut")
    public String loginOut(HttpServletRequest request ,HttpServletResponse httpServletResponse,
                           Map<String,Object> map, HttpSession session){

        //1：取出Cookie
        Cookie[] cookies = request.getCookies();
        if(null != cookies && cookies.length > 0){
            for (Cookie cookie : cookies) {
                //2: 判断COokie中是否有CSESSIONID
                if("COOKIE_NAME".equals(cookie.getName())){
                    //3:有  直接使用
                    redisUtils.remove(String.format(TOKEN_PREFIX,"rxx",cookie.getValue()));
                }
            }
        }

        CookieUtil.delLoginToken(httpServletResponse,request);
        return "there is empty!";
    }



}
