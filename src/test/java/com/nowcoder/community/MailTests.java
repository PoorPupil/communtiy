package com.nowcoder.community;

import com.nowcoder.community.utils.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMail() {
        mailClient.sendMail("2792299602@qq.com","TEST","HELLO,WORLD!");
    }

    @Test
    public void testEmail(){
        mailClient.sendMail02("2792299602@qq.com","TEST","HELLO,WORLD!");
    }

    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username","sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        mailClient.sendMail("2792299602@qq.com","TEST",content);
    }
}
