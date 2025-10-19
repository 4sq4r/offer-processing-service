package kz.offerprocessservice.model.dto.minio;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MinioMetaData {

    String fileName;
    String url;
    String format;
}
