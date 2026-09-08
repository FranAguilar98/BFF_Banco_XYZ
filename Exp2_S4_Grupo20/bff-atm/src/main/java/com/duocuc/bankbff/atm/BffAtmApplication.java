package com.duocuc.bankbff.atm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.duocuc.bankbff.core.domain.entity")
@EnableJpaRepositories(basePackages = "com.duocuc.bankbff.core.repository")
public class BffAtmApplication {
    public static void main(String[] args) {
        SpringApplication.run(BffAtmApplication.class, args);
    }
}
