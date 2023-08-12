package com.nowcoder.community.dao.mapper;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    //通过 id 查找用户
    User selectById(int id);
    // 通过 账户 查找用户
    User selectByName(String username);
    // 通过 邮箱 查找用户
    User selectByEmail(String email);
    // 通过 user 对象插入数据
    void insertUser(User user);
    // 通过 用户id 激活用户
    void updateStatus(int userId, int status);
    @Update({
            "update user set header_url=#{headerUrl} where id=#{userId}"
    })
    int updateHeader(int userId, String headerUrl);
    @Update({
            "update user set password=#{newPassword} where id=#{id}"
    })
    void updatePasswordById(Integer id, String newPassword);
}
