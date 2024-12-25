package kz.offerprocessservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseDTO extends BaseDTO {

    @NotNull
    private String name;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID cityId;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UUID merchantId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CityDTO city;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MerchantDTO merchant;
}
