package kz.offerprocessservice.model.dto.rabbit;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationResultMessage extends RabbitMessage {

    public ValidationResultMessage(UUID priceListId, boolean success) {
        super(priceListId);
        this.success = success;
    }

    private boolean success;
}
