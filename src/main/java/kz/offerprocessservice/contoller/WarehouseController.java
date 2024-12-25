package kz.offerprocessservice.contoller;

import jakarta.validation.Valid;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.WarehouseDTO;
import kz.offerprocessservice.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/warehouses/v1")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService service;

    @PostMapping
    public WarehouseDTO saveOne(@RequestBody @Valid WarehouseDTO dto) throws CustomException {
        return service.saveOne(dto);
    }

    @GetMapping("/{id}")
    public WarehouseDTO getOne(@PathVariable UUID id) throws CustomException {
        return service.getOne(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable UUID id) throws CustomException {
        service.deleteOne(id);
    }
}
