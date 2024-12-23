package kz.offerprocessservice.contoller;

import jakarta.validation.Valid;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.PointOfSaleDTO;
import kz.offerprocessservice.service.PointOfSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/points-of-sales/v1")
@RequiredArgsConstructor
public class PointOfSaleController {

    private final PointOfSaleService service;

    @PostMapping
    public PointOfSaleDTO saveOne(@RequestBody @Valid PointOfSaleDTO dto) throws CustomException {
        return service.saveOne(dto);
    }

    @GetMapping("/{id}")
    public PointOfSaleDTO getOne(@PathVariable UUID id) throws CustomException {
        return service.getOne(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable UUID id) throws CustomException {
        service.deleteOne(id);
    }
}
