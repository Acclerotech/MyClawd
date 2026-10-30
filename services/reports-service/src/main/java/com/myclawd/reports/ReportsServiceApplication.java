package com.myclawd.reports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// scanBasePackages picks up the shared platform-common components (com.myclawd.platform).
@SpringBootApplication(scanBasePackages = "com.myclawd")
public class ReportsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportsServiceApplication.class, args);
    }
}
