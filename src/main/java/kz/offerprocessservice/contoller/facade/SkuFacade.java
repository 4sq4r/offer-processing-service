package kz.offerprocessservice.contoller.facade;

import kz.offerprocessservice.mapper.SkuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SkuFacade {

    private final SkuMapper mapper;
}
