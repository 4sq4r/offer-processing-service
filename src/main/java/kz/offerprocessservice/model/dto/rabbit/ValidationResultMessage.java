package kz.offerprocessservice.model.dto.rabbit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationResultMessage extends RabbitMessage {

    public ValidationResultMessage(String priceListId, boolean success) {
        super(priceListId);
        this.success = success;
    }

    private boolean success;
}
