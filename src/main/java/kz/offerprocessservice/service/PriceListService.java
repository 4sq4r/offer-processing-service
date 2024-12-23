package kz.offerprocessservice.service;

import kz.offerprocessservice.repository.PriceListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceListService {

    private PriceListRepository priceListRepository;
}
