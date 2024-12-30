package kz.offerprocessservice.service;

import kz.offerprocessservice.model.entity.OfferEntity;
import kz.offerprocessservice.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OfferService {

    private final OfferRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public void saveAll(Set<OfferEntity> set) {
        repository.saveAll(set);
    }
}
