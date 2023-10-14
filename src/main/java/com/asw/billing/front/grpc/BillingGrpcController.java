package com.asw.billing.front.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BillingGrpcController {
    BillingGrpcImpl billingGrpc;
    @Value("${grpc.port}")
    Integer port;

    public BillingGrpcController(BillingGrpcImpl billingGrpc) {
        this.billingGrpc = billingGrpc;
    }

    @PostConstruct
    public void init(){
        Server server = ServerBuilder
                .forPort(port)
                .addService(billingGrpc)
                .build();
        try {
            server.start();
            log.info("GRPC server started on port {}", port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
