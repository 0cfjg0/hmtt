package com.test.minio;

import com.heima.common.aliyun.GreenImageScan;
import com.heima.file.config.MinIOConfig;
import com.heima.file.service.impl.MinIOFileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class Minio_test {

    @Autowired
    MinIOFileStorageService minIO;

    @Test
    public void minioTest() throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        //创建minio对应的java客户端
        MinioClient client = MinioClient
                .builder()
                .endpoint("http://192.168.200.130:9000")
                .credentials("minio", "minio123")
                .build();

        //构建上传对象
        FileInputStream input = new FileInputStream("D:\\文件迁移\\image\\test.jpg");
        PutObjectArgs obj = PutObjectArgs
                .builder()
                .bucket("cfjg")
                .object("test.jpg")
                .stream(input, input.available(), -1)//传输文件的字节流,如果知道文件大小,设置partsize为-1,如果不知道设置objectsize为-1
//                .contentType("text/html")//tomcat中的.html格式
                .contentType("image/jpeg")
                .build();

        //上传
        client.putObject(obj);
    }

    @Test
    public void fileStartersTest() throws FileNotFoundException {
        String url = minIO.uploadImgFile("cfjg", "test.jpg", new FileInputStream("D:\\文件迁移\\image\\test.jpg"));
        System.out.println(url);
    }

    @Test
    public void greenTest(){

    }


}
