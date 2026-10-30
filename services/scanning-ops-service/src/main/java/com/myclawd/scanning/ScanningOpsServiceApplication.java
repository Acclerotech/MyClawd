package com.myclawd.scanning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// scanBasePackages picks up the shared platform-common components (com.myclawd.platform).
@SpringBootApplication(scanBasePackages = "com.myclawd")
public class ScanningOpsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScanningOpsServiceApplication.class, args);
    }
}
