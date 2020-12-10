package com.orange.pocs.grpc;

import com.orange.pocs.grpc.order.grpc.service.OrderServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@EnableAsync
@SpringBootApplication
public class GrpcWithSpringBootMs2Application {

    public static void main(String[] args) {
        SpringApplication.run(GrpcWithSpringBootMs2Application.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @GrpcClient("order-service")
    private OrderServiceGrpc.OrderServiceBlockingStub stub;

    @Bean("custom-stub")
    public OrderServiceGrpc.OrderServiceBlockingStub getOrderServiceBlockingStubWithDeadLine() {
        return stub.withDeadlineAfter(3, TimeUnit.MILLISECONDS);
    }
}
