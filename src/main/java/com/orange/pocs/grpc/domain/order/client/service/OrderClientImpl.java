package com.orange.pocs.grpc.domain.order.client.service;

import com.google.protobuf.StringValue;
import com.orange.pocs.grpc.order.grpc.service.OrderServiceGrpc;
import com.orange.pocs.grpc.order.grpc.service.OrderServiceGrpc.OrderServiceBlockingStub;
import com.orange.pocs.grpc.order.grpc.service.OrderServiceOuterClass;
import com.orange.pocs.grpc.restapi.exception.NotFoundException;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderClientImpl {

    @GrpcClient("order-service")
    private OrderServiceBlockingStub stub;

    @GrpcClient("order-service")
    private OrderServiceGrpc.OrderServiceStub asyncStub;

    @Autowired
    @Qualifier("custom-stub")
    private OrderServiceBlockingStub customStub;

    public OrderServiceOuterClass.Order getOrder(String orderId) {
        try {
            Metadata metadata = new Metadata();
            metadata.put(Metadata.Key.of("MY_MD_1", Metadata.ASCII_STRING_MARSHALLER), "This is metadata of MY_MD_1");

            StringValue id = StringValue.newBuilder().setValue(orderId).build();
            return stub.withCompression("gzip").getOrder(id);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NotFoundException("Order " + orderId + " not found");
            } else {
                log.error("Oops, something happened: " + e.getMessage());
                throw e;
            }
        }
    }

    public List<String> searchOrdersContainItemStr(String itemStr) {
        try {
            List<String> ordersFound = new ArrayList<>();
            StringValue searchStr = StringValue.newBuilder().setValue("Google").build();
            Iterator<OrderServiceOuterClass.Order> matchingOrdersItr;
            matchingOrdersItr = stub.searchOrders(searchStr);
            while (matchingOrdersItr.hasNext()) {
                OrderServiceOuterClass.Order matchingOrder = matchingOrdersItr.next();
                ordersFound.add(matchingOrder.getId());
            }
            return ordersFound;
        } catch (StatusRuntimeException e) {
            if (!e.getStatus().isOk()) {
                throw new NotFoundException("Coincidences not found. Item: " + itemStr);
            } else {
                throw e;
            }
        }


    }

    public void updateOrders(List<OrderServiceOuterClass.Order> ordersToUpdate) {
        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<StringValue> updateOrderResponseObserver = new StreamObserver<StringValue>() {
            @Override
            public void onNext(StringValue value) {
                log.info("Update Orders Res : " + value.getValue());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                log.info("Update orders response  completed!");
                finishLatch.countDown();
            }
        };

        StreamObserver<OrderServiceOuterClass.Order> updateOrderRequestObserver = asyncStub.updateOrders(updateOrderResponseObserver);
        ordersToUpdate.forEach(updateOrderRequestObserver::onNext);
        if (finishLatch.getCount() == 0) {
            log.error("RPC completed or errored before we finished sending.");
            return;
        }
        updateOrderRequestObserver.onCompleted();
        try {
            if (!finishLatch.await(10, TimeUnit.SECONDS)) {
                log.error("FAILED : Process orders cannot finish within 10 seconds");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void processOrders(List<String> orderIdsToProcess) {
        final CountDownLatch finishLatch = new CountDownLatch(1);

        StreamObserver<OrderServiceOuterClass.CombinedShipment> orderProcessResponseObserver = new StreamObserver<OrderServiceOuterClass.CombinedShipment>() {
            @Override
            public void onNext(OrderServiceOuterClass.CombinedShipment value) {
                log.info("Combined Shipment : " + value.getId() + " : " + value.getOrdersListList());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                log.info("Order Processing completed!");
                finishLatch.countDown();
            }
        };

        StreamObserver<StringValue> orderProcessRequestObserver = asyncStub.processOrders(orderProcessResponseObserver);
        orderIdsToProcess.stream().map(id -> StringValue.newBuilder().setValue(id).build()).forEach(orderProcessRequestObserver::onNext);

        if (finishLatch.getCount() == 0) {
            log.error("RPC completed or errored before we finished sending.");
            return;
        }
        orderProcessRequestObserver.onCompleted();

        try {
            if (!finishLatch.await(120, TimeUnit.SECONDS)) {
                log.error("FAILED : Process orders cannot finish within 60 seconds");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
