package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.exception.PriceListActionException;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.service.PriceListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

import java.time.LocalDateTime;

import static kz.offerprocessservice.configuration.PriceListStateMachineConfiguration.PRICE_LIST_ID_HEADER;

@Slf4j
@RequiredArgsConstructor
public abstract class PriceListAction implements Action<PriceListStatus, PriceListEvent> {

    protected final PriceListService priceListService;

    @Override
    public void execute(StateContext<PriceListStatus, PriceListEvent> context) {
        String priceListId = String.valueOf(context.getMessageHeader(PRICE_LIST_ID_HEADER));
        try {
            doExecute(priceListId, context);
        } catch (Exception e) {
            log.error(
                    "PriceListAction error. action={}, id={}, from{}, to={}",
                    this.getClass().getSimpleName(),
                    priceListId,
                    context.getSource() != null ? context.getSource().getId() : "null",
                    context.getTarget() != null ? context.getTarget().getId() : "null",
                    e
            );

            throw new PriceListActionException(priceListId, e);
        }
    }

    protected abstract void doExecute(String priceListId, StateContext<PriceListStatus, PriceListEvent> context);

    protected PriceListEntity updatePriceListStatus(PriceListEntity priceListEntity, PriceListStatus priceListStatus) {
        updateStatusAndUpdatedAt(priceListEntity, priceListStatus);
        return priceListService.updateOne(priceListEntity);
    }

    protected PriceListEntity updatePriceListStatus(
            PriceListEntity priceListEntity,
            PriceListStatus priceListStatus,
            String failReason
    ) {
        updateStatusAndUpdatedAt(priceListEntity, priceListStatus);
        priceListEntity.setFailReason(failReason);
        return priceListService.updateOne(priceListEntity);
    }

    protected PriceListEntity updatePriceListStatus(
            String priceListId,
            PriceListStatus status,
            String failReason
    ) {
        PriceListEntity priceListEntity = priceListService.findEntityById(priceListId);
        updateStatusAndUpdatedAt(priceListEntity, status);
        priceListEntity.setFailReason(failReason);
        return priceListService.updateOne(priceListEntity);
    }

    protected PriceListEntity updatePriceListStatus(String priceListId, PriceListStatus status) {
        PriceListEntity priceListEntity = priceListService.findEntityById(priceListId);
        updateStatusAndUpdatedAt(priceListEntity, status);
        return priceListService.updateOne(priceListEntity);
    }

    private void updateStatusAndUpdatedAt(PriceListEntity priceListEntity, PriceListStatus status) {
        priceListEntity.setStatus(status);
        priceListEntity.setUpdatedAt(LocalDateTime.now());
    }
}
