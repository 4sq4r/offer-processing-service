package kz.offerprocessservice.contoller;

import jakarta.xml.bind.JAXBException;
import kz.offerprocessservice.facade.PriceListFacade;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.model.enums.FileFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price-lists/v1")
public class PriceListController {

    private final PriceListFacade facade;

    @PostMapping("/{merchantId}")
    public PriceListDTO upload(@PathVariable String merchantId,
                               @RequestPart MultipartFile file) throws CustomException {
        return facade.uploadPriceList(merchantId, file);
    }

    @GetMapping("/{merchantId}/template")
    public ResponseEntity<byte[]> downloadPriceListTemplate(@PathVariable String merchantId, @RequestParam FileFormat format) throws JAXBException, IOException {
        return facade.downloadTemplate(merchantId, format);
    }
}
