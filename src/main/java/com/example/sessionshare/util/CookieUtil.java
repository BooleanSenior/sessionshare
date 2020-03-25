package com.example.sessionshare.util;

import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Logger;

public class CookieUtil {
    //    private final static Logger  log = (Logger) LoggerFactory.getLogger(CookieUtil.class);

    public static String COOKIE_NAME = "COOKIE_NAME";


    //写入
    public static void writeLoginToken(HttpServletResponse response, String token) {
        Cookie ck = new Cookie(COOKIE_NAME, token);


        //设置路径
        ck.setPath("/");
        ck.setHttpOnly(true);
        //设置 cookie存活时间      -1  0   >0
        // -1  关闭浏览器后销毁  0 立刻销毁   >0 这个时间结束后销毁
        ck.setMaxAge(Math.toIntExact(RedisUtils.EXPIRE)); //保存时间
        // log.info("write cookie name:{} , cookie vlaue: {}"+ck.getName()+ck.getValue());
        response.addCookie(ck);
    }


    //删除
    public static void delLoginToken(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie ck : cks) {
                if (ck.getName().equals(COOKIE_NAME)) {
                    ck.setPath("/");
                    ck.setMaxAge(0); //0 = del
                    // log.info("del cookie name : {} ,cookie vlaue: {}"+ck.getName()+ck.getValue());
                    response.addCookie(ck);
                    return;
                }
            }
        }
    }

    //获取
    public static String getCookie(HttpServletRequest request) {
        //取出Cookie
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                //2: 判断COokie中是否有CSESSIONID
                if ("COOKIE_NAME".equals(cookie.getName())) {
                    //3:有  直接使用
                    //取缓存
                    return cookie.getValue();
                }
            }
        }

        return "";
    }
}
