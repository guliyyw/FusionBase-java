package com.yu.fusionbase.common.minio;

import com.google.common.annotations.Beta;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


//注册映射类，指定文件
//@EnableConfigurationProperties(MinioProperties.class)
//扫描注册
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
//只有当`minio.endpoint`属性存在时，该配置类才会生效
@ConditionalOnProperty(name = "minio.endpoint")
public class MinioConfiguration {

    //从application.yml配置文件中建立映射
    //@Value("&{minio.endpoint}")
    //private String endpoint;
    @Autowired
    private MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(properties.getEndpoint()).credentials(properties
                .getAccessKey(), properties.getSecretKey()).build();
    }
}

