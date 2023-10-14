package com.asw.billing.front.tcp;

import com.asw.billing.service.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@Slf4j
public class BillingTcpController {
    BillingService billingService;
    ObjectMapper jsonMapper;
    @Value("${tcp.port:8181}")
    private Integer port;
    @Value("${tcp.queue:100}")
    private Integer queue;

    public BillingTcpController(BillingService billingService, ObjectMapper jsonMapper){
        this.billingService = billingService;
        this.jsonMapper = jsonMapper;
    }

    @PostConstruct
    public void init() throws Exception {
        ServerSocket serverSocket = new ServerSocket(port, queue);
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        executorService.execute(() -> {
            log.info("TCP server started. port:{} queue:{}", port, queue);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    //log.info("client accepted");
                    Thread.ofVirtual()
                            .start(new TcpClientService(socket, billingService, jsonMapper));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
