package com.heima.wemedia.service;

import org.springframework.stereotype.Service;

@Service
public interface WmAuditService{

    public void auditMedia(Integer newsId);

}
