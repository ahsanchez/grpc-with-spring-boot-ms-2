package com.orange.pocs.grpc.domain.order.client.interceptor;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;

@GrpcGlobalClientInterceptor
@Slf4j
public class OrderClientInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> CUSTOM_HEADER_KEY =
            Metadata.Key.of("custom_client_header_key", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor,
                                                               CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                channel.newCall(methodDescriptor, callOptions)) {

            @Override
            public void sendMessage(ReqT message) {
                System.out.printf("Sending method '%s' message '%s'%n", methodDescriptor.getFullMethodName(),
                        message.toString());
                super.sendMessage(message);
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                System.out.println(OrderClientInterceptor.class.getSimpleName());

                headers.put(CUSTOM_HEADER_KEY, "customRequestValue");

                ClientCall.Listener<RespT> listener = new ForwardingClientCallListener<RespT>() {
                    @Override
                    protected Listener<RespT> delegate() {
                        return responseListener;
                    }

                    @Override
                    public void onMessage(RespT message) {
                        System.out.printf("Received message '%s'%n", message.toString());
                        super.onMessage(message);
                    }
                };

                super.start(listener, headers);
            }
        };
    }
}