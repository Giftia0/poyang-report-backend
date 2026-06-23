package com.example.poyangreportbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PoyangReportBackendApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless","false");
        System.load("C:\\Giftia\\opencv\\build\\java\\x64\\opencv_java3410.dll");
        SpringApplication.run(PoyangReportBackendApplication.class, args);
//        SpringApplicationBuilder builder = new SpringApplicationBuilder(PoyangReportBackendApplication.class);
//        builder.headless(false).web(WebApplicationType.NONE).run(args);

    }

}
