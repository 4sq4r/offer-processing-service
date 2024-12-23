package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.PriceListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PriceListRepository extends JpaRepository<PriceListEntity, UUID> {
}

