package kz.offerprocessservice.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import kz.offerprocessservice.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private static final long SIZE = 5242880;
    private final MinioClient client;

    @Value("${minio.bucket}")
    private String bucket;

    public void uploadFile(MultipartFile file, String url) throws CustomException {
        try {
            client.putObject(PutObjectArgs.builder()
                    .stream(file.getInputStream(), file.getSize(), SIZE)
                    .contentType(file.getContentType())
                    .bucket(bucket)
                    .object(url)
                    .build());
        } catch (Exception e) {
            log.error("Unable to upload file: {}\n {}", url, e.getMessage());
            throw CustomException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    public InputStream getFile(String url) throws CustomException {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(url)
                    .build());

        } catch (Exception e) {
            log.error("Unable to get file: {}\n {}", url, e.getMessage());
            throw CustomException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
}
