package kz.offerprocessservice.model.entity;

import jakarta.persistence.*;
import kz.offerprocessservice.model.enums.OfferStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "offers")
@EqualsAndHashCode(callSuper = true)
public class OfferEntity extends BaseEntity {

    @Column(name = "offer_code", nullable = false)
    private String offerCode;
    @Column(name = "offer_name", nullable = false)
    private String offerName;
    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private MerchantEntity merchant;
    @ManyToOne
    @JoinColumn(name = "sku_id")
    private SkuEntity sku;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OfferStatus status;
}
