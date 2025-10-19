package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MerchantRepository extends JpaRepository<MerchantEntity, String> {
    boolean existsByNameIgnoreCase(String name);
}
