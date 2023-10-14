package com.asw.billing.storage;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component("SynchroBillingMemoryStorage")
public class SynchroBillingMemoryStorage implements BillingStorage{
    private Map<Long,Card> storage = new HashMap<>();
    private Long i = 0L;

    @Override
    public synchronized Card createCard(String persopa) {
        Long id = i++;
        Card card = new Card(id, persopa, LocalDate.now(),0.0);
        storage.putIfAbsent(id, card);
        return card;
    }

    @Override
    public synchronized void processOperation(CardOperation operation) {
        Card card = storage.get(operation.idCard());
        if (card != null) storage.put(operation.idCard(),
                new Card(operation.idCard(), card.persona(),
                        card.createdAt(), card.ballance() + operation.amount()));
    }

    @Override
    public synchronized Card getCard(Long id) {
        return storage.getOrDefault(id, defaultCard);
    }
    @Override
    public Map printStorage() {
        return storage;
    }
}
