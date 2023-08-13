package com.nowcoder.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

//     `id` int(11) NOT NULL AUTO_INCREMENT,
    private Integer id;
//  `username` varchar(50) DEFAULT NULL,
    private String username;
//  `password` varchar(50) DEFAULT NULL,
    private String password;
//  `salt` varchar(50) DEFAULT NULL,
    private String salt;
//  `email` varchar(100) DEFAULT NULL,
    private String email;
//  `type` int(11) DEFAULT NULL COMMENT '0-普通用户; 1-超级管理员; 2-版主;',
    private Integer type;
//            `status` int(11) DEFAULT NULL COMMENT '0-未激活; 1-已激活;',
    private Integer status;
//            `activation_code` varchar(100) DEFAULT NULL,
    private String activationCode;
//  `header_url` varchar(200) DEFAULT NULL,
    private String headerUrl;
//  `create_time` timestamp NULL DEFAULT NULL,
    private Date createTime;

}
