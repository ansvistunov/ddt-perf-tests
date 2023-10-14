package com.asw.billing;


import com.asw.billing.storage.BillingMemoryStorage;
import com.asw.billing.storage.BillingStorage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@Slf4j
public class BillingMemoryStorageTests {

    static BillingStorage.Card card;
    final static String person = "xxx";

    @BeforeAll
    public static void setUp(@Autowired BillingMemoryStorage memoryStorage){
        card = memoryStorage.createCard(person);

    }




    @Test
    public void testCreateCard(@Autowired BillingMemoryStorage memoryStorage){
        String person = "testx";
        LocalDate date = LocalDate.now();

        BillingStorage.Card card = memoryStorage.createCard(person);
        log.info("card = {}", card);
        Assertions.assertEquals(card.ballance(),0,"balance must be 0");
        Assertions.assertEquals(card.persona(), person, "person must be "+person);
        Assertions.assertNotNull(card.createdAt(),"created date must be not null");
        Assertions.assertEquals(card.createdAt(), date, "created date must be "+date);
    }

    @Test
    public void testProcessOperation(@Autowired BillingMemoryStorage memoryStorage){
        Double amount = 15.2;
        BillingStorage.CardOperation cardOperation = new BillingStorage.CardOperation(card.id(), LocalDateTime.now(), amount);
        memoryStorage.processOperation(cardOperation);
        BillingStorage.Card result = memoryStorage.getCard(card.id());
        Assertions.assertEquals(result.id(), card.id(),"id must be equals");
        Assertions.assertEquals(result.ballance(), amount,"amount must be "+amount);

        cardOperation = new BillingStorage.CardOperation(card.id(), LocalDateTime.now(), -amount);
        memoryStorage.processOperation(cardOperation);
        result = memoryStorage.getCard(card.id());
        Assertions.assertEquals(result.ballance(), 0,"amount must be 0");
    }

    @Test
    public void testGetCard(@Autowired BillingMemoryStorage memoryStorage){
        BillingStorage.Card result = memoryStorage.getCard(card.id());
        Assertions.assertEquals(card.id(), result.id(), "id must be equals" );
        Assertions.assertEquals(card.persona(), result.persona(), "person must be "+person);
        Assertions.assertEquals(card.createdAt(), result.createdAt(), "created date must be equals");
    }

}
