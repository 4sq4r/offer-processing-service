package kz.offerprocessservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(value = "minio")
public class MinioProperties {

    private String url;

    private String bucket;

    private String accessKey;

    private String secretKey;

    private Boolean autoCreateBucket;

    private Integer connectTimeout;

    private Integer writeTimeout;

    private Integer readTimeout;

    private String prefixToDelete;

    private String priceListsFolder;

    private String priceListsUrl;

    private Integer partSize;

}
