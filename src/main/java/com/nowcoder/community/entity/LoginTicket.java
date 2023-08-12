package com.nowcoder.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginTicket {

    private Integer id;

    private Integer userId;

    private String ticket;
    // 0 有效，1无效
    private Integer status;
    // 过期时间
    private Date expired;
}
