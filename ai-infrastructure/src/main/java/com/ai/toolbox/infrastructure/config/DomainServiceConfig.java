package com.ai.toolbox.infrastructure.config;

import com.ai.toolbox.domain.overtime.service.OvertimeDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {

    @Bean
    public OvertimeDomainService overtimeDomainService() {
        return new OvertimeDomainService();
    }
}