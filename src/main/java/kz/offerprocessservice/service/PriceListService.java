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
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceListService {

    @Value("${minio.price-lists-folder}")
    private String priceListFolderName;
    @Value("${minio.price-lists-url}")
    private String actualMinioUrl;

    private final PriceListRepository priceListRepository;
    private final MinioService minioService;
    private final PriceListMapper priceListMapper;
    private final WarehouseService warehouseService;

    @Transactional(rollbackFor = Exception.class)
    public PriceListDTO uploadPriceList(UUID merchantId,MultipartFile file) throws CustomException {
        String[] split = file.getOriginalFilename().split("\\.");
        String salt = UUID.randomUUID().toString();
        String fileName = salt + "." + split[split.length - 1];
        String url = StringUtils.MINIO_FILE_FORMAT.formatted(priceListFolderName, fileName);
        minioService.uploadFile(file, StringUtils.MINIO_FILE_FORMAT.formatted(actualMinioUrl, fileName));

        PriceListEntity entity = new PriceListEntity();
        entity.setName(fileName);
        entity.setMerchantId(merchantId);
        entity.setOriginalName(file.getOriginalFilename());
        entity.setUrl(url);
        entity.setStatus(PriceListStatus.NEW);
        priceListRepository.save(entity);

        return priceListMapper.toDTO(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> downloadTemplate(UUID id) throws IOException {
        return FileUtils.getPriceListTemplate(warehouseService.getAllPosNames(id));
    }

    public Set<PriceListEntity> findNewPriceLists() {
        return priceListRepository.findAllNewPriceLists();
    }

    @Transactional(rollbackFor = Exception.class)
    public PriceListEntity updateStatus(PriceListEntity entity) {
        return priceListRepository.save(entity);
    }
}
