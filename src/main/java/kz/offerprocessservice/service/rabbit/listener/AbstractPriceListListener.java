package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.model.dto.rabbit.PriceListMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class AbstractPriceListListener {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    abstract void handle(PriceListMessage message);
}
