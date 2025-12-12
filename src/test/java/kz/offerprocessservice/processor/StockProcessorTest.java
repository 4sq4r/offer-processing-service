package kz.offerprocessservice.processor;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.model.entity.StockEntity;
import kz.offerprocessservice.model.entity.WarehouseEntity;
import kz.offerprocessservice.service.StockService;
import kz.offerprocessservice.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockProcessorTest {

    private static final String MERCHANT_ID = "m1";
    private static final String WH1_ID = "wh1";
    private static final String WH2_ID = "wh2";
    private static final String OFFER_CODE = "offerCode";

    @Mock
    private StockService stockService;

    @Mock
    private WarehouseService warehouseService;

    @InjectMocks
    private StockProcessor underTest;

    @Test
    void saveStocks_savesCorrectStockEntities() {
        // given
        WarehouseEntity wh1 = buildWarehouseEntity(WH1_ID);
        WarehouseEntity wh2 =buildWarehouseEntity(WH2_ID);
        when(warehouseService.getAllWarehousesByMerchantId(MERCHANT_ID))
                .thenReturn(Set.of(wh1, wh2));
        PriceListItemDTO dto = buildPriceListItem(OFFER_CODE, Map.of(WH1_ID, 10, WH2_ID, 20));
        OfferEntity offer = buildOfferEntity();
        Set<PriceListItemDTO> dtoSet = Set.of(dto);
        Set<OfferEntity> offerSet = Set.of(offer);
        // when
        underTest.saveStocks(dtoSet, offerSet, MERCHANT_ID);

        // then
        ArgumentCaptor<Set<StockEntity>> captor = ArgumentCaptor.forClass(Set.class);
        verify(stockService).saveAll(captor.capture());
        Set<StockEntity> saved = captor.getValue();
        assertThat(saved).hasSize(2);
        assertThat(saved)
                .anySatisfy(stock -> {
                    assertThat(stock.getWarehouse().getId()).isEqualTo(WH1_ID);
                    assertThat(stock.getStock()).isEqualTo(10);
                    assertThat(stock.getOffer()).isEqualTo(offer);
                })
                .anySatisfy(stock -> {
                    assertThat(stock.getWarehouse().getId()).isEqualTo(WH2_ID);
                    assertThat(stock.getStock()).isEqualTo(20);
                    assertThat(stock.getOffer()).isEqualTo(offer);
                });
    }

    @Test
    void saveStocks_doesNotCallSaveWhenNoStocks() {
        // given
        when(warehouseService.getAllWarehousesByMerchantId(MERCHANT_ID))
                .thenReturn(Set.of());
        PriceListItemDTO dto = buildPriceListItem(OFFER_CODE, Map.of());
        OfferEntity offerEntity = buildOfferEntity();
        // when
        underTest.saveStocks(Set.of(dto), Set.of(offerEntity), MERCHANT_ID);
        // then
        verify(stockService, never()).saveAll(any());
    }

    @Test
    void saveStocks_throwsException_whenOfferNotFound() {
        // given
        PriceListItemDTO dto = buildPriceListItem("unknown", Map.of(WH1_ID, 5));
        when(warehouseService.getAllWarehousesByMerchantId(MERCHANT_ID))
                .thenReturn(Set.of());
        // when & then
        assertThatThrownBy(() ->
                underTest.saveStocks(Set.of(dto), Set.of(), MERCHANT_ID)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Offer not found after saving");
    }

    private WarehouseEntity buildWarehouseEntity(String id) {
        WarehouseEntity entity = new WarehouseEntity();
        entity.setId(id);

        return entity;
    }

    private PriceListItemDTO buildPriceListItem(String offerCode, Map<String, Integer> stocks) {
        PriceListItemDTO dto = new PriceListItemDTO();
        dto.setOfferCode(offerCode);
        dto.setStocks(stocks);

        return dto;
    }

    private OfferEntity buildOfferEntity() {
        OfferEntity entity = new OfferEntity();
        entity.setOfferCode(OFFER_CODE);

        return entity;
    }

}