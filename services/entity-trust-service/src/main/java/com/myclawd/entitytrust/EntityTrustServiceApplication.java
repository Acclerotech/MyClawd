package com.myclawd.entitytrust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// scanBasePackages picks up the shared platform-common components (com.myclawd.platform).
@SpringBootApplication(scanBasePackages = "com.myclawd")
public class EntityTrustServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EntityTrustServiceApplication.class, args);
    }
}
