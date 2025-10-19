package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, String> {

    boolean existsByNameIgnoreCase(String name);
}
