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

    private final MerchantService merchantService;

    @PostMapping
    public MerchantDTO saveOne(@RequestBody @Valid MerchantDTO dto) throws CustomException {
        return merchantService.saveOne(dto);
    }

    @GetMapping("/{id}")
    public MerchantDTO getOne(@PathVariable UUID id) throws CustomException {
        return merchantService.getOne(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable UUID id) throws CustomException {
        merchantService.deleteOne(id);
    }
}
