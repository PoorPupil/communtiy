package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginReguired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired

    @LoginReguired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginReguired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        // 判断参数是否为空
        if(headerImage == null) {
            model.addAttribute("error", "传入图片是空的");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件的格式不正确");
            return "/site/setting";
        }

        //拿到图片，第一件事应该是生成文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        System.out.println(uploadPath);
        // 根据文件名，然后将图片保存到本地，
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("文件上传失败：" + e.getMessage());
            throw new RuntimeException("文件上传失败，服务器发生异常", e);
        }
        // 图片地址保存到数据库
        // thhp://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        // 回显页面，显示图片
        return "redirect:/index";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片类型
        response.setContentType("image/" + suffix);

        try (
                FileInputStream fileInputStream = new FileInputStream(fileName);
                OutputStream out = response.getOutputStream();
                ){

            byte[] bytes = new byte[1024];
            int b = 0;
            while ((b = fileInputStream.read(bytes)) != -1) {
                out.write(bytes,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：" + e.getMessage());
        }
    }

    // 修改密码
    @PostMapping("/password")
    public String updatePassword(String password, String newPassword, Model model){
        // 首先是判断 参数是否为空
        if(StringUtils.isBlank(password) || StringUtils.isBlank(newPassword)){
            model.addAttribute("passwordMsg", "密码不能为空");
            model.addAttribute("password", password);
            model.addAttribute("newPassword", newPassword);
            return "/site/setting";
        }
        // 判断旧密码是否正确
        User user = hostHolder.getUser();
        String s = CommunityUtil.md5(password + user.getSalt());
        if(!s.equals(user.getPassword())){
            model.addAttribute("passwordMsg", "密码错误");
            model.addAttribute("password", password);
            model.addAttribute("newPassword", newPassword);
            return "/site/setting";
        }

        // 直接更新
        userService.updatePasswordById(user.getId(),newPassword);

        return "redirect:/index";
    }
}
