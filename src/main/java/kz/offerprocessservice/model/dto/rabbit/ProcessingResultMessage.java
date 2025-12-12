package kz.offerprocessservice.model.dto.rabbit;

import kz.offerprocessservice.file.processing.ProcessingResultStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProcessingResultMessage extends RabbitMessage {

    private final ProcessingResultStatus status;

    public ProcessingResultMessage(String priceListId, ProcessingResultStatus status) {
        super(priceListId);
        this.status = status;
    }
}
