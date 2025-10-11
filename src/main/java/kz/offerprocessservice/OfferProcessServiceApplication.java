package kz.offerprocessservice;

import kz.offerprocessservice.configuration.MinioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MinioProperties.class)
public class OfferProcessServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfferProcessServiceApplication.class, args);
    }
}
