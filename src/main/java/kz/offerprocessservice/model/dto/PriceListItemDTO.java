package kz.offerprocessservice.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PriceListItemDTO {
    private String offerCode;
    private String offerName;
    private Map<String, Integer> stocks;
}
