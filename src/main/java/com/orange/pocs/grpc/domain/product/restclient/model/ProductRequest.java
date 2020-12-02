package com.orange.pocs.grpc.domain.product.restclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String id;
    private String name;
    private String description;
    private float price;

    public ProductRequest(String name, String description, long price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
