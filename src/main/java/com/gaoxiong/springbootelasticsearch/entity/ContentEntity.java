package com.gaoxiong.springbootelasticsearch.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @author gaoxiong
 * @ClassName ContentEntity
 * @Description TODO
 * @date 2019/1/10 21:52
 */
@Document(indexName = "content",type = "content")
@Data
public class ContentEntity implements Serializable {

    @Id
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 内容类型 1.文章,2. 问题
     */
    private Integer type;

    /**
     * 文章类别
     */
    private String category;

    /**
     * 文章阅读数
     */
    private Integer read;

    /**
     * 文章支持数
     */
    private Integer support;

}
