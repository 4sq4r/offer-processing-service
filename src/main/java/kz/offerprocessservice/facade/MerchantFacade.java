package kz.offerprocessservice.facade;

import kz.offerprocessservice.mapper.MerchantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MerchantFacade {

    private final MerchantMapper mapper;
}
