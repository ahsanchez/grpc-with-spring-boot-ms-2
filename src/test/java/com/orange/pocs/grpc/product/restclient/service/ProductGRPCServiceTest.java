package com.orange.pocs.grpc.product.restclient.service;

import com.orange.pocs.grpc.domain.product.grpc.service.ProductGRPCServiceImpl;
import com.orange.pocs.grpc.product.grpc.service.Product;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Log4j2
public class ProductGRPCServiceTest {

    @Autowired
    private ProductGRPCServiceImpl productService;

    @Test
    public void grpc_add_product_get_product_elapsed_times() throws IOException {
        double start = getTime();
        String response = productService.addProduct(getProduct());
        System.out.println("Add product elapsed time:  " + getDiffInSeconds(start, getTime()));
        start = getTime();
        productService.getProduct(response);
        System.out.println("Get product elapsed time:  " + getDiffInSeconds(start, getTime()));
    }

    private Product getProduct() {
        return Product.newBuilder().setName("LG Q6").setDescription("LG smartphone").setPrice((long) 500.25).build();
    }

    private double getTime() {
        return System.currentTimeMillis();
    }

    private double getDiffInSeconds(double start, double end) {
        return (end - start) / 1000;
    }
}
