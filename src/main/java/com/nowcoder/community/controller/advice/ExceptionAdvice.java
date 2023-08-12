package com.nowcoder.community.controller.advice;

import com.nowcoder.community.utils.CommunityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class )
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 记录异常
        logger.error("服务器发生异常", e.getMessage());
        for(StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
        // 通过请求头判断是否是 ajax请求，是的话返回数据信息，否的话跳转页面
        String xRequestedWith = request.getHeader("x-requested-with");
        if("XMLHttpRequest".equals(xRequestedWith)) {
            // 是异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常"));
        }else{
            // 页面跳转
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
