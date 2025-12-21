package uk.almenares.dynamicip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class DynamicIpApplication {

    static void main(String[] args) {
        SpringApplication.run(DynamicIpApplication.class, args);
    }

}
