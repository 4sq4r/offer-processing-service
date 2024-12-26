package kz.offerprocessservice.event;

import kz.offerprocessservice.model.entity.PriceListEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class FileValidatedEvent extends ApplicationEvent {

    private final PriceListEntity priceList;

    public FileValidatedEvent(Object source, PriceListEntity priceList) {
        super(source);
        this.priceList = priceList;
    }
}
