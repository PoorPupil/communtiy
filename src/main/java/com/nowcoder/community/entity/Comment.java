package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;
@Data
public class Comment {

    private Integer id;

    private Integer userId;

    private Integer entityType;

    private Integer entityId;

    private int targetId;

    private String content;

    private Integer status;

    private Date createTime;
}
