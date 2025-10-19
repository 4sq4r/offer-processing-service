package kz.offerprocessservice.contoller;

import jakarta.validation.Valid;
import kz.offerprocessservice.contoller.facade.WarehouseFacade;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.WarehouseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/warehouses/v1")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseFacade facade;

    @PostMapping
    public WarehouseDTO saveOne(@RequestBody @Valid WarehouseDTO dto) throws CustomException {
        return facade.saveOne(dto);
    }

    @GetMapping("/{id}")
    public WarehouseDTO getOne(@PathVariable String id) throws CustomException {
        return facade.getOne(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable String id) throws CustomException {
        facade.deleteOne(id);
    }
}
