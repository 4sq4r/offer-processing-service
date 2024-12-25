package kz.offerprocessservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "stocks")
@EqualsAndHashCode(callSuper = true)
public class StockEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private OfferEntity offer;
    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseEntity warehouse;
    @Column(nullable = false)
    private Integer stock;
}
