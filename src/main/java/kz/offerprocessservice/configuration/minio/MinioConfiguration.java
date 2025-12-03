package kz.offerprocessservice.configuration.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
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
            if (Boolean.TRUE.equals(minioProperties.getAutoCreateBucket())) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                                               .bucket(bucket)
                                               .build());
            } else {
                throw new CustomException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorMessageSource.MINIO_BUCKET_NOT_EXIST.getText(bucket)
                );
            }
        }

        return minioClient;
    }
}
