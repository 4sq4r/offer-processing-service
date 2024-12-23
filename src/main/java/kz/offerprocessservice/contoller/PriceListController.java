package kz.offerprocessservice.contoller;

import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.PriceListDTO;
import kz.offerprocessservice.service.PriceListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price-lists/v1")
public class PriceListController {

    private final PriceListService service;

    @PostMapping
    public PriceListDTO upload(@RequestPart MultipartFile file) throws CustomException {
        return service.uploadPriceList(file);
    }
}
