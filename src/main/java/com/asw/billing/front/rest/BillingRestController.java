package com.asw.billing.front.rest;

import com.asw.billing.dto.DTOCard;
import com.asw.billing.dto.DTOCardOperation;
import com.asw.billing.dto.DTOCardOperationList;
import com.asw.billing.service.BillingService;
import com.asw.billing.storage.BillingStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class BillingRestController {
    BillingService billingService;

    public Mono<ServerResponse> createCard(ServerRequest request) {
        return request.bodyToMono(String.class)
                .flatMap(person -> Mono.just(billingService.createCard(person)))
                .flatMap(card -> Mono.just(new DTOCard(card.id(), card.persona(), card.createdAt(), card.ballance())))
                .flatMap(card -> ServerResponse.created(URI.create("/cards/" + card.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(card)));
    }

    public Mono<ServerResponse> processOperation(ServerRequest request) {
        return request.bodyToMono(DTOCardOperation.class)
                .flatMap(cardOperation -> {
                    billingService.processOperation(new BillingStorage.CardOperation(
                            cardOperation.cardId(), cardOperation.operationDateTime(), cardOperation.amount()
                    ));
                    return ServerResponse.ok().build();
                });
    }

    public Mono<ServerResponse> processOperations(ServerRequest request) {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        Scheduler schedulerA = Schedulers.fromExecutorService(executorService);
        return request.bodyToMono(DTOCardOperationList.class)
                .publishOn(schedulerA)
                .flatMap((dtolist) -> {
                    List<BillingStorage.CardOperation> cardOperationList = dtolist.operationList()
                            .parallelStream()
                            .map(dto -> new BillingStorage.CardOperation(dto.cardId(), dto.operationDateTime(), dto.amount()))
                            .collect(Collectors.toList());
                    return Mono.just(billingService.processOperations(cardOperationList));
                })
                .flatMap((foo)->ServerResponse.ok().build());
    }

    public Mono<ServerResponse> getCard(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(billingService.getCard(id)));
    }

}
