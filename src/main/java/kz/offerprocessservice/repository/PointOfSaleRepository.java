package kz.offerprocessservice.repository;

import kz.offerprocessservice.model.entity.PointOfSaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface PointOfSaleRepository extends JpaRepository<PointOfSaleEntity, UUID> {
    boolean existsByNameIgnoreCaseAndMerchantId(String name, UUID merchantId);

    @Query(nativeQuery = true,
            value = """
                    select name
                    from points_of_sales
                    where merchant_id = :merchantId
                    """)
    Set<String> findAllByMerchantId(UUID merchantId);
}
