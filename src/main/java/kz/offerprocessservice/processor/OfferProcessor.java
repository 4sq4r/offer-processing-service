package kz.offerprocessservice.processor;

import kz.offerprocessservice.model.dto.PriceListItemDTO;
import kz.offerprocessservice.model.entity.MerchantEntity;
import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.model.enums.OfferStatus;
import kz.offerprocessservice.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OfferProcessor {

    private final OfferService offerService;

    public Set<OfferEntity> saveOffers(
            Set<PriceListItemDTO> priceListItemDTOSet,
            MerchantEntity merchantEntity
    ) {
        if (priceListItemDTOSet.isEmpty()) {
            return Set.of();
        }

        Set<OfferEntity> offers = priceListItemDTOSet.stream()
                .map(priceListItem -> {
                         OfferEntity offerEntity = new OfferEntity();
                         offerEntity.setOfferName(priceListItem.getOfferName());
                         offerEntity.setOfferCode(priceListItem.getOfferCode());
                         offerEntity.setMerchant(merchantEntity);
                         offerEntity.setStatus(OfferStatus.NEW);

                         return offerEntity;
                     }
                )
                .collect(Collectors.toSet());

        return offerService.saveAll(offers);
    }
}
