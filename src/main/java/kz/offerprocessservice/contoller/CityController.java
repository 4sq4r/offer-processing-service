package kz.offerprocessservice.contoller;

import jakarta.validation.Valid;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.model.dto.CityDTO;
import kz.offerprocessservice.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cities/v1")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping
    public CityDTO saveOne(@RequestBody @Valid CityDTO dto) throws CustomException {
        return cityService.saveOne(dto);
    }

    @GetMapping("/{id}")
    public CityDTO getOne(@PathVariable UUID id) throws CustomException {
        return cityService.getOne(id);
    }

    @DeleteMapping("/{id}")
    public void deleteOne(@PathVariable UUID id) throws CustomException {
        cityService.deleteOne(id);
    }
}
