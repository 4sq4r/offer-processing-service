package kz.offerprocessservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CityDTO extends BaseDTO {

    @NotBlank(message = "City name must be not null.")
    private String name;
}
