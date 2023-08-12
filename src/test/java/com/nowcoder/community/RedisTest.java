package com.nowcoder.community;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
@SpringBootTest
//@ContextConfiguration(classes =CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void test001(){
        // 存数据
        String redisKey = "x";
        redisTemplate.opsForValue().set(redisKey, 1);
        // 取数据
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        // 增加
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        // 减少
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void test002() {
        String rediskey = "test:user";
        redisTemplate.opsForHash().put(rediskey, "id", 1);
        redisTemplate.opsForHash().put(rediskey, "username", "id");
        System.out.println(redisTemplate.opsForHash().get(rediskey,"id"));
    }
}
