package com.asw.billing.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service("BillingMemoryStorage")
@Slf4j
public class BillingMemoryStorage implements BillingStorage{

    ConcurrentHashMap<Long, Card> storage = new ConcurrentHashMap<>();
    AtomicLong sequence = new AtomicLong(0);

    @Override
    public Card createCard(String persopa) {
        Long id = sequence.getAndIncrement();
        Card card = new Card(id, persopa, LocalDate.now(),0.0);
        storage.putIfAbsent(id, card);
        return card;
    }

    @Override
    public void processOperation(CardOperation operation) {
        storage.computeIfPresent(operation.idCard(), (id,card)-> new Card(card.id(), card.persona(), card.createdAt(), card.ballance()+ operation.amount()));
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
