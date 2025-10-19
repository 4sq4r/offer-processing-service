package kz.offerprocessservice.model.dto;

import kz.offerprocessservice.model.PriceListState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PriceListDTO extends BaseDTO {

    private String name;
    private String originalName;
    private String url;
    private String merchantId;
    private PriceListState status;
}
