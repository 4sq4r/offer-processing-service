package kz.offerprocessservice.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import kz.offerprocessservice.configuration.minio.MinioProperties;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.minio.MinioMetaData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioProperties minioProperties;
    private final MinioClient client;

    public MinioMetaData uploadFile(MultipartFile file) {
        String[] split = file.getOriginalFilename().split("\\.");
        String format = split[split.length - 1];
        String name = UUID.randomUUID() + "." + format;
        String url = minioProperties.getFileFormat().formatted(minioProperties.getPriceListsUrl(), name);
        try {
            client.putObject(
                    PutObjectArgs.builder()
                            .stream(file.getInputStream(), file.getSize(), minioProperties.getPartSize())
                            .contentType(file.getContentType())
                            .bucket(minioProperties.getBucket())
                            .object(url)
                            .build()
            );

            return MinioMetaData.builder()
                    .fileName(name)
                    .url(url)
                    .format(format)
                    .build();
        } catch (Exception e) {
            log.error("Unable to upload file: {}\n {}", url, e.getMessage());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public InputStream getFile(String url) {
        String trimmedUrl = url.replaceFirst(minioProperties.getPrefixToDelete(), "");
        try {
            return client.getObject(GetObjectArgs.builder()
                                            .bucket(minioProperties.getBucket())
                                            .object(trimmedUrl)
                                            .build());

        } catch (Exception e) {
            log.error("Unable to get file: {}\n {}", trimmedUrl, e.getMessage());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
