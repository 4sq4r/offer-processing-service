package kz.offerprocessservice.service;

import jakarta.xml.bind.JAXBException;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.templating.FileTemplatingStrategy;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.repository.PriceListRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import kz.offerprocessservice.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceListService {

    private final PriceListRepository priceListRepository;
    private final WarehouseService warehouseService;
    private final FileStrategyProviderImpl fileStrategyProvider;

    public PriceListEntity savePriceList(
            MerchantEntity merchantEntity,
            MultipartFile file,
            String fileName,
            String url,
            String format
    ) {
        PriceListEntity entity = new PriceListEntity();
        entity.setName(fileName);
        entity.setMerchant(merchantEntity);
        entity.setOriginalName(file.getOriginalFilename());
        entity.setUrl(url);
        entity.setStatus(PriceListState.UPLOADED);
        entity.setFormat(FileFormat.fromExtension("." + format));

        return priceListRepository.save(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<byte[]> downloadTemplate(String merchantId, FileFormat format) throws IOException, JAXBException {
        Set<String> warehouseNames = warehouseService.getAllWarehouseNamesByMerchantId(merchantId);
        FileTemplatingStrategy strategy = fileStrategyProvider.getTemplatingStrategy(format);
        Set<String> extendedNames = new LinkedHashSet<>();

        if (!format.equals(FileFormat.XML)) {
            extendedNames.add(FileUtils.OFFER_CODE);
            extendedNames.add(FileUtils.OFFER_NAME);
        }

        extendedNames.addAll(warehouseNames);

        return strategy.generate(extendedNames);
    }

    @Transactional(rollbackFor = Exception.class)
    public PriceListEntity findEntityById(String id) throws CustomException {
        return priceListRepository.findById(id).orElseThrow(
                () -> CustomException.builder()
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message(ErrorMessageSource.PRICE_LIST_NOT_FOUND.getText(id.toString()))
                        .build()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOne(PriceListEntity priceListEntity) {
        priceListRepository.save(priceListEntity);
    }

    public PriceListState getCurrentState(String id) throws CustomException {
        return findEntityById(id).getStatus();
    }
}
