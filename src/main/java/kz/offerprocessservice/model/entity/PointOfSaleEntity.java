package kz.offerprocessservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "points_of_sales")
@EqualsAndHashCode(callSuper = true)
public class PointOfSaleEntity extends BaseEntity {

    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private MerchantEntity merchant;
    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;
}
