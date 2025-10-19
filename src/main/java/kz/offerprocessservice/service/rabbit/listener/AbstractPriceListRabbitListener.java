package kz.offerprocessservice.service.rabbit.listener;

import kz.offerprocessservice.service.statemachine.PriceListStateMachineService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public abstract class AbstractPriceListRabbitListener {

    protected final PriceListStateMachineService priceListStateMachineService;
    protected final Logger log = LoggerFactory.getLogger(getClass());
}
