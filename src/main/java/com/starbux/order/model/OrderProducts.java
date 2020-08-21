package com.starbux.order.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@EqualsAndHashCode(exclude = {"orders"})
@Builder
@Entity
@Table(name = "order_products")
public class OrderProducts implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id")
    private String id;

    @OneToMany(mappedBy = "orderProducts", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderDrinkTopping> orderDrinkToppingList;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders orders;

    public void addToOrderDrinkTopping(OrderDrinkTopping orderDrinkTopping) {
        orderDrinkTopping.setOrderProducts(this);
        if (this.orderDrinkToppingList == null) {
            this.orderDrinkToppingList = new ArrayList<>();
        }
        this.orderDrinkToppingList.add(orderDrinkTopping);
    }

}
