package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.utils.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @GetMapping("/index")
    public String index(Model model, Page page){
        // 在方法调用前，SpringMVC 会自动 注入 model 和 page，并把page 保存到 model中
        // 所以在 thymeleaf 中可以直接通过model 调用 page
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        // 根据传入的参数 查询 列表
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());

        List<Map<String, Object>> discussPost = new ArrayList<>();
        // 通过循环，将列表里的每个 discussPost 拿出来，
        // 1、 discussPost 的数据放到 map里
        // 2、 通过拿出来的 discussPost 的 userId ，查询获取 user 的数据，作为user对象存储到 map 里
        // 3、 将操作完毕的map放到列表中
        for (DiscussPost post : discussPosts){
            Map<String, Object> map = new HashMap<>();
            map.put("post",post);
            User user = userService.findUserById(post.getUserId());
            map.put("user", user);

            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            map.put("likeCount", likeCount);
            discussPost.add(map);
        }
        model.addAttribute("discussPost",discussPost);
        return "index";
    }

    /**
     * 跳转到错误页面
     * @return
     */
    @GetMapping("/error")
    public String getErrorPage() {
        return "error/500";
    }
}
