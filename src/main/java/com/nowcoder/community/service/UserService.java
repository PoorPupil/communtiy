package com.nowcoder.community.service;

import com.nowcoder.community.dao.mapper.LoginTicketMapper;
import com.nowcoder.community.dao.mapper.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityConstant;
import com.nowcoder.community.utils.CommunityUtil;
import com.nowcoder.community.utils.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private LoginTicketMapper loginTicketMapper;


    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;



    public User findUserById(int id) {
        return userMapper.selectById(id);
    }


    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null){
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        // 查看账户名是否已存在
        User u = userMapper.selectByName(user.getUsername());
        if(u != null) {
            map.put("usernameMsg","账户已存在");
            return map;
        }
        //查看邮箱是否已存在
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null) {
            map.put("emailMsg","邮箱已被注册");
            return map;
        }
        // 补全新用户的数据
        // 盐
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        // md5 密码+盐
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        // 类型
        user.setType(0);
        // 状态
        user.setStatus(0);
        // 验证码
        user.setActivationCode(CommunityUtil.generateUUID());
        // 头像,这里是使用 牛客网放在网上的资源
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        // 创建时间
        user.setCreateTime(new Date());

        // 将 user 插入数据库中
        userMapper.insertUser(user);
        user = userMapper.selectByName(user.getUsername());

        // 激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation",context);

        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }

    /**
     * 激活用户
     */
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }

    // 账号登录
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 账号不能为空
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }

        // 密码不能为空
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        // 验证账号是否存在
        User user = userMapper.selectByName(username);
        if(user == null) {
            map.put("usernameMsg", "账户不存在");
            return map;
        }

        // 验证状态
        if(user.getStatus() == 0) {
            map.put("usernameMsg", "账号未激活");
            return map;
        }

        //验证密码是否正确
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg", " 密码不正确");
            return map;
        }


        // 生成凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }


    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    // 根据用户id更新用户的头像新路径
    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    // 根据用户id 更改用户密码
    public void updatePasswordById(Integer id, String newPassword) {
        userMapper.updatePasswordById(id, newPassword);
    }

    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }
}
