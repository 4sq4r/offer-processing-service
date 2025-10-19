package kz.offerprocessservice.configuration.minio;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties(value = "minio")
public class MinioProperties {

    String url;

    String bucket;

    String accessKey;

    String secretKey;

    Boolean autoCreateBucket;

    Integer connectTimeout;

    Integer writeTimeout;

    Integer readTimeout;

    String prefixToDelete;

    String priceListsFolder;

    String priceListsUrl;

    Integer partSize;

    String fileFormat;
}
