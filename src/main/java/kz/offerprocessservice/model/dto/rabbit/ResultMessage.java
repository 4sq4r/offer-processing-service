package kz.offerprocessservice.model.dto.rabbit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResultMessage extends RabbitMessage {

    private final boolean success;

    public ResultMessage(String priceListId, boolean success) {
        super(priceListId);
        this.success = success;
    }
}
