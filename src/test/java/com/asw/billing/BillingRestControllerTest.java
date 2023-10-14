package com.asw.billing;

import com.asw.billing.config.BillingConfiguration;
import com.asw.billing.dto.DTOCard;
import com.asw.billing.dto.DTOCardOperation;
import com.asw.billing.dto.DTOCardOperationList;
import com.asw.billing.front.rest.BillingRestController;
import com.asw.billing.service.BillingService;
import com.asw.billing.storage.BillingStorage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@WebFluxTest(controllers = BillingRestController.class)
@Import(BillingConfiguration.class)
@Slf4j
public class BillingRestControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    BillingService service;

    @Test
    void testCreateCard(@Autowired BillingService billingService){
        BillingStorage.Card card = new BillingStorage.Card(1L, "testPerson", LocalDate.now(), 0.0);
        DTOCard dtoCard = new DTOCard(1L, "testPerson", LocalDate.now(), 0.0);
        Mockito.when(service.createCard("testPerson")).thenReturn(card);
        webClient.post()
                .uri("/cards")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("testPerson"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DTOCard.class)
                .isEqualTo(dtoCard);
    }

    @Test
    void testGetCard(){
        BillingStorage.Card card = new BillingStorage.Card(1L, "testPerson", LocalDate.now(), 0.0);
        Mockito.when(service.getCard(1L)).thenReturn(card);
        webClient.get()
                .uri("/cards/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BillingStorage.Card.class)
                .isEqualTo(card);
    }

    @Test
    void testCardOperations(){
        DTOCardOperation dtoCardOperation = new DTOCardOperation(1L, LocalDateTime.now(),10.0);
        DTOCardOperationList operationList = new DTOCardOperationList(List.of(dtoCardOperation));
        BillingStorage.CardOperation cardOperation = new BillingStorage.CardOperation(1L, dtoCardOperation.operationDateTime(), 10.0);
        Mockito.when(service.processOperation(cardOperation)).thenReturn(true);
        webClient.post()
                .uri("/cardoperations")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(operationList))
                .exchange()
                .expectStatus().isOk();

    }
}
