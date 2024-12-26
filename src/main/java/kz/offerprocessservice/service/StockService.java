package kz.offerprocessservice.service;

import kz.offerprocessservice.model.entity.StockEntity;
import kz.offerprocessservice.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public Set<StockEntity> saveAll(Set<StockEntity> stocks) {
        if (!stocks.isEmpty()) {
            return new HashSet<>(repository.saveAll(stocks));
        }

        return new HashSet<>();
    }
}
