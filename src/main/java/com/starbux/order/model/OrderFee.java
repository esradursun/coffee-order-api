package com.starbux.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_fee")
public class OrderFee implements Serializable {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id")
    private String id;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "discount_total")
    private Double discountTotal;

    @Column(name = "discount_type")
    private DiscountType discountType;

    @Column(name = "total_price")
    private Double totalPrice;

    @OneToOne(mappedBy = "orderFee")
    @JsonBackReference
    private Orders orders;
}
