package com.nowcoder.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

//@ToString
@Data//所有属性的get和set方法、toString 方法、hashCode方法、equals方法
@AllArgsConstructor
@NoArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "discusspost")
//@Document(indexName = "discusspost", createIndex = false)
public class DiscussPost {
//public class DiscussPost implements Serializable {

    //`id` int(11) NOT NULL AUTO_INCREMENT,
    @Id
    private Integer id;
    //  `user_id` varchar(45) DEFAULT NULL,
    @Field(type = FieldType.Integer)
    private Integer userId;
    //  `title` varchar(100) DEFAULT NULL,
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    //  `content` text,
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;
    //          `type` int(11) DEFAULT NULL COMMENT '0-普通; 1-置顶;',
    @Field(type = FieldType.Integer)
    private Integer type;
    //          `status` int(11) DEFAULT NULL COMMENT '0-正常; 1-精华; 2-拉黑;',
    @Field(type = FieldType.Integer)
    private Integer status;
    //          `create_time` timestamp NULL DEFAULT NULL,
    @Field(type = FieldType.Date)
    private Date createTime;
    //  `comment_count` int(11) DEFAULT NULL,
    @Field(type = FieldType.Integer)
    private Integer commentCount;
    //  `score` double DEFAULT NULL,
    @Field(type = FieldType.Double)
    private Double score;

}
