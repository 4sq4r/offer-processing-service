package kz.offerprocessservice.service;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.PriceListState;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.repository.PriceListRepository;
import kz.offerprocessservice.util.ErrorMessageSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceListService {

    private final PriceListRepository priceListRepository;

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
    public PriceListEntity findEntityById(String id) {
        return priceListRepository.findById(id).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, ErrorMessageSource.PRICE_LIST_NOT_FOUND.getText(id))
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOne(PriceListEntity priceListEntity) {
        priceListRepository.save(priceListEntity);
    }

    public PriceListState getCurrentState(String id) {
        return findEntityById(id).getStatus();
    }
}
