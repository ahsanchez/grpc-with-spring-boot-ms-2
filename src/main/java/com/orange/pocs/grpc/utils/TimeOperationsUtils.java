package com.orange.pocs.grpc.utils;

import org.springframework.stereotype.Component;

@Component
public class TimeOperationsUtils {

    public double getNowTimeMillis() {
        return System.currentTimeMillis();
    }

    public double getElapsedTimeInSeconds(double start, double end) {
        return (end - start) / 1000;
    }

}
