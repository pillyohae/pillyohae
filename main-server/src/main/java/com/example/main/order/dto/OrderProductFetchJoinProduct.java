package com.example.main.order.dto;

import com.example.common.order.entity.OrderProduct;
import com.example.common.product.entity.Product;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class OrderProductFetchJoinProduct {
    OrderProduct orderProduct;
    Product product;
    @QueryProjection
    public OrderProductFetchJoinProduct(OrderProduct orderProduct, Product product) {
        this.orderProduct = orderProduct;
        this.product = product;
    }
}
