package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.file.processing.FileProcessingStrategy;
import kz.offerprocessservice.file.processing.ProcessingResultStatus;
import kz.offerprocessservice.model.PriceListEvent;
import kz.offerprocessservice.model.PriceListStatus;
import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.model.entity.PriceListEntity;
import kz.offerprocessservice.processor.OfferProcessor;
import kz.offerprocessservice.processor.StockProcessor;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.PriceListService;
import kz.offerprocessservice.service.rabbit.producer.PriceListProcessingProducer;
import kz.offerprocessservice.service.statemachine.action.ActionNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Set;

@Slf4j
@Component(ActionNames.START_PROCESSING)
public class ProcessingAction extends PriceListAction {

    private final StockProcessor stockProcessor;
    private final OfferProcessor offerProcessor;
    private final MerchantService merchantService;
    private final FileStrategyProviderImpl fileStrategyProvider;
    private final MinioService minioService;
    private final PriceListProcessingProducer priceListProcessingProducer;

    public ProcessingAction(
            PriceListService priceListService,
            StockProcessor stockProcessor,
            MerchantService merchantService,
            FileStrategyProviderImpl fileStrategyProvider,
            MinioService minioService,
            OfferProcessor offerProcessor,
            PriceListProcessingProducer priceListProcessingProducer
    ) {
        super(priceListService);
        this.stockProcessor = stockProcessor;
        this.merchantService = merchantService;
        this.fileStrategyProvider = fileStrategyProvider;
        this.minioService = minioService;
        this.offerProcessor = offerProcessor;
        this.priceListProcessingProducer = priceListProcessingProducer;
    }

    @Override
    public void doExecute(String priceListId, StateContext<PriceListStatus, PriceListEvent> context) {
        PriceListEntity priceListEntity = updatePriceListStatus(priceListId, PriceListStatus.PROCESSING);
        ProcessingResultStatus resultStatus = processFile(priceListEntity);
        priceListProcessingProducer.sendProcessingResult(priceListId, resultStatus);
    }

    private ProcessingResultStatus processFile(PriceListEntity priceListEntity) {
        try (InputStream inputStream = minioService.getFile(priceListEntity.getUrl())) {
            MerchantEntity merchantEntity = merchantService.findEntityById(priceListEntity.getMerchant().getId());
            FileProcessingStrategy fileProcessingStrategy = fileStrategyProvider.getProcessingStrategy(
                    priceListEntity.getFormat()
            );
            Set<PriceListItemDTO> priceListItems = fileProcessingStrategy.extract(inputStream);

            if (priceListItems.isEmpty()) {
                return ProcessingResultStatus.FAIL;
            }

            return processAndSaveStocks(priceListItems, merchantEntity)
                    ? ProcessingResultStatus.SUCCESS
                    : ProcessingResultStatus.FAIL;
        } catch (Exception e) {
            return ProcessingResultStatus.FAIL;
        }
    }

    private boolean processAndSaveStocks(Set<PriceListItemDTO> priceListItems, MerchantEntity merchantEntity) {
        Set<OfferEntity> offers = offerProcessor.saveOffers(priceListItems, merchantEntity);
        String merchantId = merchantEntity.getId();

        if (offers.isEmpty()) {
            log.warn("No offers found for merchant: {}", merchantId);
            return false;
        }

        stockProcessor.saveStocks(priceListItems, offers, merchantId);
        return true;
    }
}
