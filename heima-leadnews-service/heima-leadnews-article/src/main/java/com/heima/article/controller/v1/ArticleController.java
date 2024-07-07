package com.heima.article.controller.v1;

import com.heima.article.service.ArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author CFJG
 */
@RestController
@RequestMapping("/api/v1/article")
@Slf4j
public class ArticleController {

    @Resource
    ArticleService articleService;

    private final short LOADTYPE_LOAD_LOAD = 0;

    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto) {
        log.warn("-----"+dto.toString());
        return articleService.load(LOADTYPE_LOAD_LOAD,dto);
    }

    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto) {
        return articleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,dto);
    }

    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto) {
        return articleService.load(ArticleConstants.LOADTYPE_LOAD_NEW,dto);
    }
}
