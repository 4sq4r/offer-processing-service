package kz.offerprocessservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SkuDTO extends BaseDTO {

    @NotNull
    private String name;
}
