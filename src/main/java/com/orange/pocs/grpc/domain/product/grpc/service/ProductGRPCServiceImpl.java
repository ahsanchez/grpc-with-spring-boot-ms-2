package com.orange.pocs.grpc.domain.product.grpc.service;

import com.orange.pocs.grpc.adapter.enums.Method;
import com.orange.pocs.grpc.adapter.enums.Protocol;
import com.orange.pocs.grpc.adapter.sevice.RegisterElapsedTimesServiceImpl;
import com.orange.pocs.grpc.product.grpc.service.Product;
import com.orange.pocs.grpc.product.grpc.service.ProductId;
import com.orange.pocs.grpc.product.grpc.service.ProductServiceGrpc;
import com.orange.pocs.grpc.utils.TimeOperationsUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ProductGRPCServiceImpl {

    @Value("${remote.server.grpc.host}")
    private String host;

    @Value("${remote.server.grpc.port}")
    private int port;

    @Autowired
    private RegisterElapsedTimesServiceImpl registerElapsedTimesService;

    @Autowired
    private TimeOperationsUtils timeOperationsUtils;

    public String addProduct(Product product) throws IOException {
        double start = timeOperationsUtils.getNowTimeMillis();
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        ProductServiceGrpc.ProductServiceBlockingStub stub = ProductServiceGrpc.newBlockingStub(channel);
        ProductId productId = stub.addProduct(product);
        channel.shutdown();
        registerElapsedTimesService.writeEntry(Protocol.GRPC, Method.ADD_PRODUCT, timeOperationsUtils.getElapsedTimeInSeconds(start, timeOperationsUtils.getNowTimeMillis()));
        return productId.getValue();
    }

    public Product getProduct(String productId) throws IOException {
        double start = timeOperationsUtils.getNowTimeMillis();
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        ProductServiceGrpc.ProductServiceBlockingStub stub = ProductServiceGrpc.newBlockingStub(channel);
        Product product = stub.getProduct(ProductId.newBuilder().setValue(productId).build());
        channel.shutdown();
        registerElapsedTimesService.writeEntry(Protocol.GRPC, Method.GET_PRODUCT, timeOperationsUtils.getElapsedTimeInSeconds(start, timeOperationsUtils.getNowTimeMillis()));
        return product;
    }
}
