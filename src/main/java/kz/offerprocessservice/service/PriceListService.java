package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.PriceListMapper;
import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.PriceListStatus;
import kz.offerprocessservice.repository.PriceListRepository;
import kz.offerprocessservice.util.FileUtils;
import kz.offerprocessservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceListService {

    @Value("${minio.price-lists-folder}")
    private String PRICE_LIST_FOLDER_NAME;
    @Value("${minio.price-lists-url}")
    private String MINIO_URL;

    private final PriceListRepository priceListRepository;
    private final MinioService minioService;
    private final PriceListMapper priceListMapper;
    private final PointOfSaleService pointOfSaleService;

    @Transactional(rollbackFor = Exception.class)
    public PriceListDTO uploadPriceList(MultipartFile file) throws CustomException {
        String[] split = file.getOriginalFilename().split("\\.");
        String salt = UUID.randomUUID().toString();
        String fileName = salt + "." + split[split.length - 1];
        String url = StringUtils.MINIO_FILE_FORMAT.formatted(PRICE_LIST_FOLDER_NAME, fileName);
        minioService.uploadFile(file, StringUtils.MINIO_FILE_FORMAT.formatted(MINIO_URL, fileName));

        PriceListEntity entity = new PriceListEntity();
        entity.setName(fileName);
        entity.setMerchantId(UUID.randomUUID());
        entity.setOriginalName(file.getOriginalFilename());
        entity.setUrl(url);
        entity.setStatus(PriceListStatus.UPLOADED);
        priceListRepository.save(entity);

        return priceListMapper.toDTO(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> downloadTemplate(UUID id) throws IOException {
        return FileUtils.getPriceListTemplate(pointOfSaleService.getAllPosNames(id));
    }
}
