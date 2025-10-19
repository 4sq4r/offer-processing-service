package kz.offerprocessservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WarehouseDTO extends BaseDTO {

    @NotNull
    private String name;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cityId;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String merchantId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CityDTO city;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private MerchantDTO merchant;
}
