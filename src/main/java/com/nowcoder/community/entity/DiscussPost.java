package com.nowcoder.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
//@ToString
@Data//所有属性的get和set方法、toString 方法、hashCode方法、equals方法
@AllArgsConstructor
@NoArgsConstructor
public class DiscussPost {

//`id` int(11) NOT NULL AUTO_INCREMENT,
  private Integer id;
//  `user_id` varchar(45) DEFAULT NULL,
  private Integer userId;
//  `title` varchar(100) DEFAULT NULL,
  private String title;
//  `content` text,
  private String content;
//          `type` int(11) DEFAULT NULL COMMENT '0-普通; 1-置顶;',
  private Integer type;
//          `status` int(11) DEFAULT NULL COMMENT '0-正常; 1-精华; 2-拉黑;',
  private Integer status;
//          `create_time` timestamp NULL DEFAULT NULL,
  private Date createTime;
//  `comment_count` int(11) DEFAULT NULL,
  private Integer commentCount;
//  `score` double DEFAULT NULL,
  private Double score;

}
