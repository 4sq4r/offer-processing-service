package kz.offerprocessservice.contoller.facade;

import kz.offerprocessservice.mapper.CityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CityFacade {

    private final CityMapper mapper;
}
