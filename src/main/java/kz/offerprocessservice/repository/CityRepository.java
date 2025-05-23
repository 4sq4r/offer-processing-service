package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, UUID> {

    boolean existsByNameIgnoreCase(String name);
}
