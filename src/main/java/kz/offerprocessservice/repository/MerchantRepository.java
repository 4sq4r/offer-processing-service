package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
