package com.heima.user.Interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;

import com.heima.user.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头
        String UserId = request.getHeader("UserId");
        if(ObjectUtil.isEmpty(UserId)){
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        //2.放到ThreadLocal中
        try {
            Integer userId = Convert.toInt(UserId);
            ThreadLocalUtils.setCurrentUser(userId);
            log.info(ThreadLocalUtils.getCurrentUser().toString());
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
        ThreadLocalUtils.removeCurrentUser();
    }
}

