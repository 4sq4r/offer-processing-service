package kz.offerprocessservice.contoller;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.service.PriceListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price-lists/v1")
public class PriceListController {

    private final PriceListService service;

    @PostMapping("/{merchantId}")
    public PriceListDTO upload(@PathVariable UUID merchantId,
                               @RequestPart MultipartFile file) throws CustomException {
        return service.uploadPriceList(merchantId, file);
    }

    @GetMapping("/{id}/template")
    public ResponseEntity<byte[]> downloadPriceListTemplate(@PathVariable UUID id) throws IOException {
        return service.downloadTemplate(id);
    }
}
