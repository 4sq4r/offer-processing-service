package kz.offerprocessservice.contoller;

import jakarta.validation.Valid;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.MerchantDTO;
import kz.offerprocessservice.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/merchants/v1")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService service;

    @PostMapping
    public MerchantDTO saveOne(@RequestBody @Valid MerchantDTO dto) throws CustomException {
        return service.saveOne(dto);
    }

    @GetMapping("/{id}")
    public MerchantDTO getOne(@PathVariable UUID id) throws CustomException {
        return service.getOne(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable UUID id) throws CustomException {
        service.deleteOne(id);
    }
}
