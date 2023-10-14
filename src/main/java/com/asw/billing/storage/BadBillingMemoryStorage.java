package com.asw.billing.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component("BadBillingMemoryStorage")
@Slf4j
public class BadBillingMemoryStorage implements BillingStorage{
    Map<Long,Card> storage = new HashMap<>();
    Long i = 0L;

    @Override
    public Card createCard(String persopa) {
        Long id = i++;
        Card card = new Card(id, persopa, LocalDate.now(),0.0);
        storage.putIfAbsent(id, card);
        return card;
    }

    @Override
    public void processOperation(CardOperation operation) {
        Card card = storage.get(operation.idCard());
        if (card != null) storage.put(operation.idCard(),
                new Card(operation.idCard(), card.persona(),
                        card.createdAt(), card.ballance() + operation.amount()));
    }

    @Override
    public Card getCard(Long id) {
        return storage.getOrDefault(id, defaultCard);
    }
    @Override
    public Map printStorage() {
        return storage;
    }
}
