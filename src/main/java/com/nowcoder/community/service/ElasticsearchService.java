package com.nowcoder.community.service;

import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussRepository;
    @Resource
    private ElasticsearchTemplate elasticTemplate;

    /**
     * 增加一个内容
     * @param post
     */
    public void saveDiscussPost(DiscussPost post) {
        discussRepository.save(post);
    }

    /**
     * 通过文章 id 删除文章
     * @param id
     */
    public void deleteDiscussPost(int id) {
        discussRepository.deleteById(id);
    }

    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        //查询标准构建，匹配字段“content”和“title”中包含“你是谁啊”关键字的数据
        //在这里使用matches，而不是contains，contains必须包含完整的关键字，而matches不需要，如果要匹配更多字段使用or或者and。
        Criteria criteria = new Criteria("title").matches(keyword)
                .or(new Criteria("content").matches(keyword));

        //高亮查询
        List<HighlightField> highlightFieldList = new ArrayList<>();
        HighlightField highlightField = new HighlightField("title",
                HighlightFieldParameters.builder().withPreTags("<em>").withPostTags("</em>").build());
        highlightFieldList.add(highlightField);
        highlightField = new HighlightField("content",
                HighlightFieldParameters.builder().withPreTags("<em>").withPostTags("</em>").build());
        highlightFieldList.add(highlightField);

        Highlight highlight = new Highlight(highlightFieldList);
        HighlightQuery highlightQuery = new HighlightQuery(highlight,DiscussPost.class);

        //构建查询
        CriteriaQueryBuilder builder = new CriteriaQueryBuilder(criteria)
                .withSort(Sort.by(Sort.Direction.DESC,"type"))
                .withSort(Sort.by(Sort.Direction.DESC,"score"))
                .withSort(Sort.by(Sort.Direction.DESC,"createTime"))
                .withHighlightQuery(highlightQuery)
                .withPageable(PageRequest.of(0,10));
        CriteriaQuery query = new CriteriaQuery(builder);
        //通过elasticsearchTemplate查询
        SearchHits<DiscussPost> result = elasticTemplate.search(query, DiscussPost.class);
        //处理结果
        List<SearchHit<DiscussPost>> searchHitList = result.getSearchHits();
        List<DiscussPost> discussPostList = new ArrayList<>();
        for(SearchHit<DiscussPost> hit:searchHitList){
            DiscussPost post = hit.getContent();
            //将高亮结果添加到返回的结果类中显示
            var titleHighlight = hit.getHighlightField("title");
            if(titleHighlight.size()!=0){
                post.setTitle(titleHighlight.get(0));
            }
            var contentHighlight = hit.getHighlightField("content");
            if(contentHighlight.size()!=0){
                post.setContent(contentHighlight.get(0));
            }
            discussPostList.add(post);

        }
        //构建Page对象
        Page<DiscussPost> page = new PageImpl<>(discussPostList, PageRequest.of(current,limit), result.getTotalHits());
        return page;
    }
}
