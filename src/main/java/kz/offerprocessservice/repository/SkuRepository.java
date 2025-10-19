package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.SkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuRepository extends JpaRepository<SkuEntity, String> {
    boolean existsByNameIgnoreCase(String name);
}
