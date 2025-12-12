package kz.offerprocessservice.processor;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.model.enums.OfferStatus;
import kz.offerprocessservice.service.OfferService;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferProcessorTest {

    @Mock
    private OfferService offerService;

    @InjectMocks
    private OfferProcessor underTest;

    @Test
    void saveOffers_returnsEmptySet_whenPriceListItemsIsEmpty() {
        //when
        Set<OfferEntity> result = underTest.saveOffers(Set.of(), new MerchantEntity());
        //then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void saveOffers_savesOffers() {
        //given
        MerchantEntity merchantEntity = Instancio.create(MerchantEntity.class);
        PriceListItemDTO priceListItemDTO = Instancio.create(PriceListItemDTO.class);
        OfferEntity offerEntity = Instancio.create(OfferEntity.class);
        offerEntity.setMerchant(merchantEntity);
        offerEntity.setStatus(OfferStatus.NEW);
        when(offerService.saveAll(anySet())).thenReturn(Set.of(offerEntity));
        //when
        Set<OfferEntity> result = underTest.saveOffers(Set.of(priceListItemDTO), merchantEntity);
        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(Set.of(offerEntity));
    }

}