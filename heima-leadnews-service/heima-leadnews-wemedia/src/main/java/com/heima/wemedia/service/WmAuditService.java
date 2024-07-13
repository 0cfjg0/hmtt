package com.heima.wemedia.service;

import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.stereotype.Service;

@Service
public interface WmAuditService{

    public void auditMedia(Integer newsId);

    public void updateNews(WmNews news, WmNews.Status status, String reason);

    public Long saveArticle(WmNews news);

}
