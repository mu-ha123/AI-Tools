package com.ai.toolbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ai.toolbox")
@EntityScan(basePackages = "com.ai.toolbox.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.ai.toolbox.infrastructure.persistence.repository")
public class AiToolboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiToolboxApplication.class, args);
    }
}
