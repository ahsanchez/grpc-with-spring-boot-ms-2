package com.orange.pocs.grpc.domain.order;

import com.orange.pocs.grpc.domain.order.client.service.OrderClientImpl;
import com.orange.pocs.grpc.order.grpc.service.OrderServiceOuterClass;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Log4j2
public class OrderClientTest {

    @Autowired
    private OrderClientImpl orderClient;

    @Test
    public void grpc_get_order() {
        double start = getTime();
        OrderServiceOuterClass.Order order = orderClient.getOrder("102");
        System.out.print("Get order Response: " + order.toString());
        System.out.println("Elapsed time:  " + getDiffInSeconds(start, getTime()));
    }

    @Test
    public void grpc_search_orders() {
        double start = getTime();
        List<String> orders = orderClient.searchOrdersContainItemStr("Google");
        System.out.println("Search orders Response: ");
        orders.forEach(System.out::println);
        System.out.println("Elapsed time:  " + getDiffInSeconds(start, getTime()));
    }

    @Test
    public void grpc_update_orders() {
        OrderServiceOuterClass.Order updOrder1 = OrderServiceOuterClass.Order.newBuilder()
                .setId("102")
                .addItems("Google Pixel 3A").addItems("Google Pixel Book")
                .setDestination("Mountain View, CA")
                .setPrice(1100)
                .build();
        OrderServiceOuterClass.Order updOrder2 = OrderServiceOuterClass.Order.newBuilder()
                .setId("103")
                .addItems("Apple Watch S4").addItems("Mac Book Pro").addItems("iPad Pro")
                .setDestination("San Jose, CA")
                .setPrice(2800)
                .build();
        OrderServiceOuterClass.Order updOrder3 = OrderServiceOuterClass.Order.newBuilder()
                .setId("104")
                .addItems("Google Home Mini").addItems("Google Nest Hub").addItems("iPad Mini")
                .setDestination("Mountain View, CA")
                .setPrice(2200)
                .build();

        double start = getTime();
        orderClient.updateOrders(Arrays.asList(updOrder1, updOrder2, updOrder3));
        System.out.println("Elapsed time:  " + getDiffInSeconds(start, getTime()));
    }


    @Test
    public void grpc_process_orders() {
        double start = getTime();
        orderClient.processOrders(Arrays.asList("102", "103", "104", "101"));
        System.out.println("Elapsed time:  " + getDiffInSeconds(start, getTime()));
    }

    private double getTime() {
        return System.currentTimeMillis();
    }

    private double getDiffInSeconds(double start, double end) {
        return (end - start) / 1000;
    }
}
