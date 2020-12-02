package com.orange.pocs.grpc.domain.product.restclient.service;

import com.orange.pocs.grpc.adapter.enums.Method;
import com.orange.pocs.grpc.adapter.enums.Protocol;
import com.orange.pocs.grpc.adapter.sevice.RegisterElapsedTimesServiceImpl;
import com.orange.pocs.grpc.domain.product.restclient.model.ProductIdResponseRequest;
import com.orange.pocs.grpc.domain.product.restclient.model.ProductRequest;
import com.orange.pocs.grpc.utils.TimeOperationsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class ProductRestServiceImpl {

    @Value("${remote.server.restapi.url}")
    private String url;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RegisterElapsedTimesServiceImpl registerElapsedTimesService;

    @Autowired
    private TimeOperationsUtils timeOperationsUtils;

    public String addProduct(ProductRequest request) throws IOException {
        double start = timeOperationsUtils.getNowTimeMillis();
        ProductIdResponseRequest response = restTemplate.postForObject(url, new HttpEntity<>(request), ProductIdResponseRequest.class);
        registerElapsedTimesService.writeEntry(Protocol.REST, Method.ADD_PRODUCT, timeOperationsUtils.getElapsedTimeInSeconds(start, timeOperationsUtils.getNowTimeMillis()));
        return response != null ? response.getValue() : "";
    }

    public ProductRequest getProduct(String productId) throws IOException {
        double start = timeOperationsUtils.getNowTimeMillis();
        ProductRequest response = restTemplate.getForObject(url + "/" + productId, ProductRequest.class);
        registerElapsedTimesService.writeEntry(Protocol.REST, Method.GET_PRODUCT, timeOperationsUtils.getElapsedTimeInSeconds(start, timeOperationsUtils.getNowTimeMillis()));
        return response;
    }
}
