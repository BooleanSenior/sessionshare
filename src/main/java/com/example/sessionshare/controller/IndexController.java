package com.example.sessionshare.controller;

import com.alibaba.fastjson.JSONArray;
import com.example.sessionshare.pojo.Student;
import com.example.sessionshare.util.CookieUtil;
import com.example.sessionshare.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.sessionshare.util.MapUtil.objectToMap;

@RestController
@RequestMapping("/index")
public class IndexController {

    /** token 前缀. */
    String TOKEN_PREFIX = "token.%s.%s";


    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisUtils redisUtils;


    @RequestMapping("/login")
    public String login(Student student,
                        HttpServletResponse httpServletResponse,
                        Map<String,Object> map, HttpSession session) throws IllegalAccessException {
        // 数据库匹配
       //通过关键字 查询用户 模拟用户
        student = new Student(1,"rxx","23");

        if (student == null){   // 判断用户是否存在
            return "用户不存在";
        }

        String token = session.getId();   //获取登录 token
        String json = JSONArray.toJSON(student).toString();
        redisUtils.setKey(
                String.format(TOKEN_PREFIX,"rxx",token),
                json,
                redisUtils.EXPIRE,
                TimeUnit.SECONDS
                );
        //设置 token 至 cookie
        CookieUtil.writeLoginToken(httpServletResponse,token);
        return "登陆成功！";
    }

    /**
     *
     * @param request
     * @param response
     * @param
     * @param session
     * @return
     */
    @RequestMapping("/loginGet")
    public Map<String , Object> loginGet(HttpServletRequest request, HttpServletResponse response,
                            HttpSession session){
        Map<String,Object> map = new HashMap<>();
        map.put("msg","there is empty!");
        String value = CookieUtil.getCookie(request);
        if(value != ""){
            map.put("msg","ok");
            Student stu = JSONArray.parseObject(redisUtils.getKey(String.format(TOKEN_PREFIX,"rxx",value)),Student.class) ;
            map.put("stu",stu);
            return map;
        }
        return map;
    }


    @RequestMapping("/loginOut")
    public String loginOut(HttpServletRequest request , HttpServletResponse httpServletResponse,
                           Map<String,Object> map, HttpSession session){
        String value = CookieUtil.getCookie(request);
        if(value != ""){
            CookieUtil.delLoginToken(httpServletResponse,request);
            redisUtils.remove(String.format(TOKEN_PREFIX,"rxx",value));
            return "ok";
        }


        return "there is empty!";
    }


}
