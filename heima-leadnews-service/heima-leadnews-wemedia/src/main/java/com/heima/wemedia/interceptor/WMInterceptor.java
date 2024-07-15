package com.heima.wemedia.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.heima.common.exception.CustomException;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.wemedia.utils.WMThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class WMInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头
        String wmUserId = request.getHeader("wmUserId");
        String UserId = request.getHeader("UserId");
        if(ObjectUtil.isAllEmpty(wmUserId,UserId)){
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        wmUserId = ObjectUtil.isEmpty(wmUserId)?UserId:wmUserId;
        //2.放到ThreadLocal中
        try {
            Integer userId = Convert.toInt(wmUserId);
            WMThreadLocalUtils.setCurrentUser(userId);
            log.info(WMThreadLocalUtils.getCurrentUser().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 释放资源
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WMThreadLocalUtils.removceCurrentUser();
    }
}

