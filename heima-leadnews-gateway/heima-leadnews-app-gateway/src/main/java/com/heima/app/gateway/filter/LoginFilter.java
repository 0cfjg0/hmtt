package com.heima.app.gateway.filter;

import cn.hutool.core.util.ObjectUtil;
import com.heima.utils.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class LoginFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if(request.getURI().getPath().matches(".*login.*")){
            //放行
            return chain.filter(exchange);
        }else{
            //判断jwtToken是否正确
            //获取token
            List<String> list = request.getHeaders().get("token");

            //token为空
            if(ObjectUtil.isEmpty(list)){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.setComplete();
            }

            String token = list.get(0);

            //检测token
            Claims claimsRes = null;
            try {
                claimsRes = AppJwtUtil.getClaimsBody(token);
                //-1：有效，0：有效，1：过期，2：过期
                int res = AppJwtUtil.verifyToken(claimsRes);
                if(res != -1 && res != 0){
                    //token过期
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    response.setComplete();
                }
            } catch (Exception e) {
                //token无效
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                response.setComplete();
            }

            //token有效
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
