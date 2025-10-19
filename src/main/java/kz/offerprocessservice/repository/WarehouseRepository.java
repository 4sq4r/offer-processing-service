package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseEntity, String> {
    boolean existsByNameIgnoreCaseAndMerchantId(String name, String merchantId);

    @Query(nativeQuery = true,
            value = """
                    select name
                    from warehouses
                    where merchant_id = :merchantId
                    """)
    Set<String> findAllNamesByMerchantId(String merchantId);

    Set<WarehouseEntity> findAllByMerchantId(String merchantId);
}
