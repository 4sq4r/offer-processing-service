package kz.offerprocessservice.contoller.facade;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.PriceListMapper;
import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.processor.PriceListProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class PriceListFacade {

    private final PriceListProcessor processor;
    private final PriceListMapper mapper;

    public PriceListDTO uploadPriceList(String merchantId, MultipartFile priceList) throws CustomException {
        return mapper.toDTO(processor.uploadPriceList(merchantId, priceList));
    }
}
