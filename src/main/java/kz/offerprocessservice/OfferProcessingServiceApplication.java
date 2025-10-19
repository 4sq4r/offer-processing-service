package kz.offerprocessservice;

import kz.offerprocessservice.configuration.minio.MinioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MinioProperties.class)
public class OfferProcessingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OfferProcessingServiceApplication.class, args);
    }
}
