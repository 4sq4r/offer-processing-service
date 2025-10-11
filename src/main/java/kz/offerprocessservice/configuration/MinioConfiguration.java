package kz.offerprocessservice.configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@RequiredArgsConstructor
public class MinioConfiguration {

    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException,
            InternalException, CustomException {
        MinioClient minioClient = new MinioClient.Builder()
                .endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
        String bucket = minioProperties.getBucket();
        boolean isBucketExist = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucket)
                        .build()
        );
        minioClient.setTimeout(
                minioProperties.getConnectTimeout(),
                minioProperties.getWriteTimeout(),
                minioProperties.getReadTimeout()
        );

        if (!isBucketExist) {
            if (minioProperties.getAutoCreateBucket()) {
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
