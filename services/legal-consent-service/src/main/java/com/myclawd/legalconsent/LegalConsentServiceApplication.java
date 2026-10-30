package com.myclawd.legalconsent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// scanBasePackages picks up the shared platform-common components (com.myclawd.platform).
@SpringBootApplication(scanBasePackages = "com.myclawd")
public class LegalConsentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegalConsentServiceApplication.class, args);
    }
}
