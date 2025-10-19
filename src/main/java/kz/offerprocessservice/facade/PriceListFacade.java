package kz.offerprocessservice.facade;

import jakarta.xml.bind.JAXBException;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.mapper.PriceListMapper;
import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.processor.PriceListProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PriceListFacade {

    private final PriceListProcessor processor;
    private final PriceListMapper mapper;

    public PriceListDTO uploadPriceList(String merchantId, MultipartFile priceList) throws CustomException {
        return mapper.toDTO(processor.uploadPriceList(merchantId, priceList));
    }

    public ResponseEntity<byte[]> downloadTemplate(String merchantId, FileFormat format) throws JAXBException, IOException {
        return processor.downloadTemplate(merchantId, format);
    }
}
