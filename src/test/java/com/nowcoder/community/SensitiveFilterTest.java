package com.nowcoder.community;

import com.nowcoder.community.utils.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SensitiveFilterTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test01(){
        String text = "这里可以赌博，哈哈哈";
        text = sensitiveFilter.filter(text);
        System.out.println(text);
    }
}
