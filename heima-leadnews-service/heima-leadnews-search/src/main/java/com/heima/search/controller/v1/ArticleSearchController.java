package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.dto.UserSearchDto;
import com.heima.search.service.ArticleSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/article/search")
@Slf4j
public class ArticleSearchController {

    @Autowired
    private ArticleSearchService articleSearchService;

    /**
     * 搜索框搜索
     * @param dto
     * @return
     * @throws IOException
     */
    @PostMapping("/search")
    public ResponseResult search(@RequestBody UserSearchDto dto) throws Exception {
        log.warn("dto:{}", dto);
        return articleSearchService.search(dto);
    }
}