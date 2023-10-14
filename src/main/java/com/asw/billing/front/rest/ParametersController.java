package com.asw.billing.front.rest;

import com.asw.billing.common.Parameters;
import com.asw.billing.service.BillingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class ParametersController {

    Parameters parameters;

    BillingService billingService;


    public Mono<ServerResponse> setParameter(ServerRequest request){
        String name = request.queryParam("name").get();
        parameters.setStorageClassname(name);
        log.info("name={}. object={}", parameters.getStorageClassName(), parameters);
        return ServerResponse.ok().build();
    }


    public Mono<ServerResponse> printStorage(ServerRequest request){
        return ServerResponse.ok().
                body(BodyInserters.fromValue(billingService.printStorage()));
    }
}
