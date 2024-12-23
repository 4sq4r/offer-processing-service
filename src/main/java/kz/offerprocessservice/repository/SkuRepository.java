package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.SkuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SkuRepository extends JpaRepository<SkuEntity, UUID> {
    boolean existsByNameIgnoreCase(String name);

}
