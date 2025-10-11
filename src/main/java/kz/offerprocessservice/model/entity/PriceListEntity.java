package kz.offerprocessservice.model.entity;

import jakarta.persistence.*;
import kz.offerprocessservice.model.enums.FileFormat;
import kz.offerprocessservice.statemachine.PriceListState;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@Entity
@Table(name = "price_lists")
@EqualsAndHashCode(callSuper = true)
public class PriceListEntity extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String name;
    @Column(name = "original_name", nullable = false, updatable = false)
    private String originalName;
    @Column(nullable = false, updatable = false)
    private String url;
    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PriceListState status;
    @Column(name = "fail_reason")
    private String failReason;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileFormat format;
}
