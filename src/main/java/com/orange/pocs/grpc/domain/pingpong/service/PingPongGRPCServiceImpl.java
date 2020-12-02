package com.orange.pocs.grpc.domain.pingpong.service;

import com.orange.pocs.grpc.pingpong.service.PingPongServiceGrpc;
import com.orange.pocs.grpc.pingpong.service.PingRequest;
import com.orange.pocs.grpc.pingpong.service.PongResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PingPongGRPCServiceImpl {

    @Value("${remote.server.grpc.host}")
    private String host;

    @Value("${remote.server.grpc.port}")
    private int port;

    public void ping() {
        log.info("ping");
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        PingPongServiceGrpc.PingPongServiceBlockingStub stub = PingPongServiceGrpc.newBlockingStub(channel);
        PongResponse helloResponse = stub.ping(PingRequest.newBuilder().setPing("").build());
        channel.shutdown();
        log.info(helloResponse.getPong());
    }
}