package com.myclawd.signature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// scanBasePackages picks up the shared platform-common components (com.myclawd.platform).
@SpringBootApplication(scanBasePackages = "com.myclawd")
public class SignatureServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignatureServiceApplication.class, args);
    }
}
