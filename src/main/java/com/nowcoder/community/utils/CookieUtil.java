package com.nowcoder.community.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name){

        // 判断参数是否为空
        if(request == null || name == null){
            throw new IllegalArgumentException("CookieUtil。getValue的参数不能为空");
        }

        //获取cookie
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
