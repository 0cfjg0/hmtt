package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.vos.SearchArticleVo;
import com.heima.search.dto.UserSearchDto;
import com.heima.search.service.ArticleSearchService;
import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author ghy
 * @version 1.0.1
 * @date 2024-07-14 17:44:22
 */
@Service
@Slf4j
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public ResponseResult search(UserSearchDto dto) throws Exception {
        if(StringUtils.isBlank(dto.getSearchWords())){
            return ResponseResult.okResult(new ArrayList<SearchArticleVo>());
        }
        int start = dto.getFromIndex();
        SearchRequest request = new SearchRequest("app_info_article");
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //搜索框匹配
        boolQuery.must(QueryBuilders.multiMatchQuery(dto.getSearchWords(), "title", "content"))
                //发布时间匹配
                .filter(QueryBuilders.rangeQuery("publishTime").lt(dto.getMinBehotTime().getTime()));
        request.source()
                .query(boolQuery)
                //排序
                .sort("publishTime", SortOrder.DESC)
                //分页
                .from(start)  //起始文档下标
                .size(dto.getPageSize())  //页大小
                //高亮
                .highlighter(
                        SearchSourceBuilder.highlight()
                                .field("title")   //高亮字段
                                .preTags("<font style='color: red; font-size: inherit;'>")   //前缀
                                .postTags("</font>")   //后缀
                                .requireFieldMatch(false)
                );

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //获取搜索hits结果
        SearchHit[] hits = response.getHits().getHits();
        ArrayList<SearchArticleVo> voList = new ArrayList<>(hits.length);
        //查询数据
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            SearchArticleVo vo = JSON.parseObject(json, SearchArticleVo.class);
            //获取高亮的内容
            Map<String, HighlightField> map = hit.getHighlightFields();
            if(!Collections.isEmpty(map)){
                HighlightField hf = map.get("title");
                if(hf != null) {
                    String filedName = hf.getFragments()[0].toString();
                    //前端已经把高亮字段固定了  h_title
                    vo.setH_title(filedName);
                }
            }
            voList.add(vo);
        }
        return ResponseResult.okResult(voList);
    }
}