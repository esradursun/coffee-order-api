package com.starbux.order.model;


import java.io.Serializable;

public enum OrderStatus implements Serializable {
    CREATED, UPDATED, FINISHED, CANCELLED;
}
