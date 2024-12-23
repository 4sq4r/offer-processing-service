package kz.offerprocessservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import kz.offerprocessservice.model.enums.PriceListStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "price_lists")
@EqualsAndHashCode(callSuper = true)
public class PriceListEntity extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String name;
    @Column(nullable = false, updatable = false)
    private String url;
    @Column(nullable = false)
    private PriceListStatus status;
}
