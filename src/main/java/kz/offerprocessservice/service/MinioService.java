package kz.offerprocessservice.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import kz.offerprocessservice.configuration.MinioProperties;
import kz.offerprocessservice.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioProperties minioProperties;
    private final MinioClient client;

    public void uploadFile(MultipartFile file, String url) throws CustomException {
        try {
            client.putObject(PutObjectArgs.builder()
                    .stream(file.getInputStream(), file.getSize(), minioProperties.getPartSize())
                    .contentType(file.getContentType())
                    .bucket(minioProperties.getBucket())
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
        String trimmedUrl = url.replaceFirst(minioProperties.getPrefixToDelete(), "");
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(trimmedUrl)
                    .build());

        } catch (Exception e) {
            log.error("Unable to get file: {}\n {}", trimmedUrl, e.getMessage());
            throw CustomException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }
}
