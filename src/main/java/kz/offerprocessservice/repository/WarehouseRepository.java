package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, UUID> {
    boolean existsByNameIgnoreCaseAndMerchantId(String name, UUID merchantId);

    @Query(nativeQuery = true,
            value = """
                    select name
                    from points_of_sales
                    where merchant_id = :merchantId
                    """)
    Set<String> findAllByMerchantId(UUID merchantId);
}
