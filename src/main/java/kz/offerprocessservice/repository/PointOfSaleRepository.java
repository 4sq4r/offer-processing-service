package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.PointOfSaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PointOfSaleRepository extends JpaRepository<PointOfSaleEntity, UUID> {
    boolean existsByNameIgnoreCaseAndMerchantId(String name, UUID merchantId);
}
