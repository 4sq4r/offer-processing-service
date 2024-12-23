package kz.offerprocessservice.model.dto;

import kz.offerprocessservice.model.enums.PriceListStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class PriceListDTO extends BaseDTO {

    private String name;
    private String originalName;
    private String url;
    private UUID merchantId;
    private PriceListStatus status;
}
