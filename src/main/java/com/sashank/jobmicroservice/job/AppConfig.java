package com.sashank.jobmicroservice.job;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class AppConfig {
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        logger.info("Initializing LoadBalanced RestTemplate for Eureka service discovery");
        
        // Configure timeout and buffer
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(
            new SimpleClientHttpRequestFactory() {{
                setConnectTimeout(5000); // 5 seconds
                setReadTimeout(5000);    // 5 seconds
            }}
        );
        
        RestTemplate restTemplate = new RestTemplate(factory);
        logger.info("RestTemplate initialized with 5 second timeout");
        return restTemplate;
    }
}
