package com.agriconnect.farmerservice;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class FarmerServiceApplication {

	private static final Logger log = 
		    LoggerFactory.getLogger(FarmerServiceApplication.class);
	
    public static void main(String[] args) {
        Environment env = SpringApplication.run(
                FarmerServiceApplication.class, args).getEnvironment();

        log.info("""
                
                --------------------------------------------------
                Application started successfully!
                Name        : {}
                Port        : {}
                Profile     : {}
                Health URL  : http://localhost:{}/actuator/health
                API BaseURL : http://localhost:{}/api/v1/farmers
                --------------------------------------------------
                """,
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                env.getProperty("spring.profiles.active", "default"),
                env.getProperty("server.port"),
                env.getProperty("server.port")
        );
    }
}
