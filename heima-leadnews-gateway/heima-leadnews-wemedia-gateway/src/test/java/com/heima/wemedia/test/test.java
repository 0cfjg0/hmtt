package com.heima.wemedia.test;

import com.heima.common.aliyun.GreenTextScan;
import com.heima.wemedia.gateway.WemediaGatewayAplication;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;

@SpringBootTest()
public class test {
    @Autowired
    GreenTextScan greenTextScan;

    @Test
    public void greenTest() throws Exception {
        System.out.println(greenTextScan.greeTextScan("常岩大傻逼"));
    }
}
