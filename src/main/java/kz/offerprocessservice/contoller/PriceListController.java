package kz.offerprocessservice.contoller;

import kz.offerprocessservice.contoller.facade.PriceListFacade;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.PriceListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price-lists/v1")
public class PriceListController {

    private final PriceListFacade facade;

    @PostMapping("/{merchantId}")
    public PriceListDTO upload(@PathVariable UUID merchantId,
                               @RequestPart MultipartFile file) throws CustomException {
        return facade.uploadPriceList(merchantId, file);
    }

//    @GetMapping("/{merchantId}/template")
//    public ResponseEntity<byte[]> downloadPriceListTemplate(@PathVariable UUID merchantId, @RequestParam FileFormat format) throws JAXBException, IOException {
//        return service.downloadTemplate(merchantId, format);
//    }
}
