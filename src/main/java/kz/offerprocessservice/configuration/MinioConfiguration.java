package kz.offerprocessservice.configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.util.ErrorMessageSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MinioConfiguration {

    @Value("${minio.url}")
    private String url;
    @Value("${minio.bucket}")
    private String bucket;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.auto-create-bucket}")
    private boolean autoCreateBucket;
    @Value("${minio.connect-timeout}")
    private int connectTimeout;
    @Value("${minio.write-timeout}")
    private int writeTimeout;
    @Value("${minio.read-timeout}")
    private int readTimeout;

    @Bean
    public MinioClient minioClient() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException, CustomException {
        MinioClient minioClient = new MinioClient.Builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        boolean isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucket).build());
        minioClient.setTimeout(connectTimeout, writeTimeout, readTimeout);

        if (!isBucketExist) {
            if (autoCreateBucket) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build());
            } else {
                throw CustomException.builder()
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message(ErrorMessageSource.MINIO_BUCKET_NOT_EXIST.getText(bucket))
                        .build();
            }
        }

        return minioClient;
    }
}
