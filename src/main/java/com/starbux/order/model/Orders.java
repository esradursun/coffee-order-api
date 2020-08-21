package com.starbux.order.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Entity(name = "Orders")
@Table(name = "orders")
public class Orders implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id")
    private String id;

    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "customer_name")
    private String customerName;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_fee_id")
    private OrderFee orderFee;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    private List<OrderProducts> productList;


    public void addToOrderProducts(OrderProducts orderProducts) {
        orderProducts.setOrders(this);
        if (this.productList == null) {
            this.productList = new ArrayList<>();
        }
        this.productList.add(orderProducts);
    }

}
