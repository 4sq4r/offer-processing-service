package kz.offerprocessservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class FileUploadedEvent extends ApplicationEvent {

    private final UUID priceListId;

    public FileUploadedEvent(Object source, UUID priceListId) {
        super(source);
        this.priceListId = priceListId;
    }
}
