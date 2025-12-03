package kz.offerprocessservice.service.statemachine.action.impl;

import kz.offerprocessservice.file.FileStrategyProviderImpl;
import kz.offerprocessservice.service.MerchantService;
import kz.offerprocessservice.service.MinioService;
import kz.offerprocessservice.service.OfferService;
import kz.offerprocessservice.service.StockService;
import kz.offerprocessservice.service.WarehouseService;
import kz.offerprocessservice.service.statemachine.action.AbstractPriceListActionTest;
import org.mockito.Mock;

class ProcessingActionTest extends AbstractPriceListActionTest<ProcessingAction> {

    @Mock
    private OfferService offerService;

    @Mock
    private StockService stockService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private WarehouseService warehouseService;

    @Mock
    private FileStrategyProviderImpl fileStrategyProvider;

    @Mock
    private MinioService minioService;

    @Override
    protected ProcessingAction createAction() {
        return new ProcessingAction(
                offerService,
                stockService,
                merchantService,
                priceListService,
                warehouseService,
                fileStrategyProvider,
                minioService
        );
    }
}