package com.nowcoder.community;

import com.nowcoder.community.dao.mapper.DiscussPostMapper;
import com.nowcoder.community.dao.mapper.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.utils.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
import java.util.Random;

//@RunWith(SpringRunner.class)
@SpringBootTest
//@ContextConfiguration(classes = CommunityApplication.class)
public class MybatisTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 测试 按作者id查询文章 以及文章数
     */
    @Test
    public void test01(){
        int counts = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(counts);
        System.out.println("--------------");
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        System.out.println(discussPosts);
    }

    @Test
    public void test02() {
        User user = userMapper.selectById(11);
        System.out.println(user);
    }

    /**
     * 测试 userMapper.selectByName(String username)
     */
    @Test
    public void test03() {
        User user = userMapper.selectByName("liubei");
        System.out.println(user);
    }

    /**
     * 测试 userMapper.selectByEmail(String email)
     */
    @Test
    public void test04() {
        User user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }
    /**
     * 测试 userMapper.insertUser(User user)
     */
    @Test
    public void test05() {
        User user = new User();
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
        userMapper.insertUser(user);
    }
    /**
     * 测试 userMapper.updateStatus(userId, status)
     */
    @Test
    public void test06() {
        userMapper.updateStatus(155,1);
    }

    //测试 user表的更新头像路径
    //http://images.nowcoder.com/head/345t.png
    @Test
    public void test07(){
        userMapper.updateHeader(165, "http://images.nowcoder.com/head/345t.png");
    }

    @Test
    public void test08(){
        DiscussPost post = new DiscussPost();
        post.setUserId(1);
        post.setTitle("zzl");
        post.setContent("zzl");
        post.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(post);
    }

}
