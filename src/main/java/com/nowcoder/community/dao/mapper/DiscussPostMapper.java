package com.nowcoder.community.dao.mapper;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 *  mapper 接口，
 *  对接 discuss_post 表的 crud 操作
 */
@Mapper
public interface DiscussPostMapper {
    /**
     * 通过 用户 id 查询用户自己的文章列表，
     * 当 userId == 0 的时候，表示查询所有文章， userId 不用作为查询条件
     * offset 每一页的起始行的行号
     * limit 每一页查询条数
     * @param userId
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId,int offset, int limit);

    /**
     * 查询总的文章数
     * userId 当为 0 的时候表示查询所有，当不为零的时候表示查询 指定作者的文章
     *
     * 当参数是用于 动态拼接sql 并且  只有一个 的时候，参数必须取别名（@Param("别名")）
     *
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 发布文章
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     *  根据文章id 查询文章的详细信息。
     * @param id 文章的id
     * @return
     */
    @Select({
            "select * from discuss_post where id = #{id}"
    })
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新帖子的评论数
     * @param id 文章id
     * @param commentCount 评论数
     * @return 更新行数
     */
    @Update({
            "update discuss_post set comment_count = #{commentCount} where id = #{id}"
    })
    int updateCommentCount(int id, int commentCount);

}
