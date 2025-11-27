package kz.offerprocessservice.contoller;

import jakarta.validation.Valid;
import kz.offerprocessservice.exception.CustomException;
import kz.offerprocessservice.facade.implementation.DefaultCityFacade;
import kz.offerprocessservice.model.dto.CityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cities/v1")
@RequiredArgsConstructor
public class CityController {

    private final DefaultCityFacade facade;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CityDTO saveOne(@RequestBody @Valid CityDTO dto) throws CustomException {
        return facade.saveOne(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CityDTO getOne(@PathVariable String id) throws CustomException {
        return facade.findOne(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOne(@PathVariable String id) throws CustomException {
        facade.deleteOne(id);
    }
}
