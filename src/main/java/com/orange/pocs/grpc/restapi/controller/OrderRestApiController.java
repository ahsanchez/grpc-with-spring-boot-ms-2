package com.orange.pocs.grpc.restapi.controller;

import com.orange.pocs.grpc.domain.product.grpc.service.ProductGRPCServiceImpl;
import com.orange.pocs.grpc.domain.product.restclient.model.ProductRequest;
import com.orange.pocs.grpc.domain.product.restclient.service.ProductRestServiceImpl;
import com.orange.pocs.grpc.product.grpc.service.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping(path = "/ms2/order")
@RestController
@Slf4j
public class OrderRestApiController {

    @Autowired
    private ProductGRPCServiceImpl grpcService;

    @Autowired
    private ProductRestServiceImpl restService;


    @PostMapping("/rest")
    public ResponseEntity<String> addProduct(@RequestBody ProductRequest request) throws IOException {
        String response = restService.addProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/rest/{id}")
    public ResponseEntity<ProductRequest> getProduct(@PathVariable String id) throws IOException {
        ProductRequest response = restService.getProduct(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/grpc")
    public ResponseEntity<String> addProductGRPC(@RequestBody ProductRequest request) throws IOException {
        String response = grpcService.addProduct(Product.newBuilder().setName(request.getName()).setDescription(request.getDescription()).setPrice(request.getPrice()).build());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/grpc/{id}")
    public ResponseEntity<ProductRequest> getProductGRPC(@PathVariable String id) throws IOException {
        Product product = grpcService.getProduct(id);
        return new ResponseEntity<>(new ProductRequest(product.getId(), product.getName(), product.getDescription(), product.getPrice()), HttpStatus.OK);
    }
}
