package kz.offerprocessservice.contoller;

import jakarta.validation.Valid;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.SkuDTO;
import kz.offerprocessservice.service.SkuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/skus/v1")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService service;

    @PostMapping
    public SkuDTO saveOne(@RequestBody @Valid SkuDTO dto) throws CustomException {
        return service.saveOne(dto);
    }

    @GetMapping("/{id}")
    public SkuDTO getOne(@PathVariable UUID id) throws CustomException {
        return service.getOne(id);
    }
}
