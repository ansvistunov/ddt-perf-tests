package com.asw.billing.service;

import com.asw.billing.common.Parameters;
import com.asw.billing.storage.BillingStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class BillingService {
    Parameters parameters;
    Map<String, BillingStorage> storages;
    int[] delayInNanos = {10, 5, 6, 12};
    @Value("${billing.service.usedelay}")
    Boolean useDelay;

    static AtomicLong operationCount = new AtomicLong(0);

    public BillingService(Parameters parameters, Map<String, BillingStorage> storages) {
        this.parameters = parameters;
        this.storages = storages;
    }


    public BillingStorage.Card createCard(String cardHolder){
        return storages.get(parameters.getStorageClassName()).createCard(cardHolder);
    }

    public boolean processOperation(BillingStorage.CardOperation operation){
        delayForCardOperation(operation);
        storages.get(parameters.getStorageClassName()).processOperation(operation);
        return true;
    }

    public boolean processOperations(List<BillingStorage.CardOperation> operations){
        BillingStorage storage = storages.get(parameters.getStorageClassName());
        for (BillingStorage.CardOperation operation: operations){
            delayForCardOperation(operation);
            storage.processOperation(operation);
        }
        return true;
    }

    public BillingStorage.Card getCard(Long id){
        return storages.get(parameters.getStorageClassName()).getCard(id);
    }


    public Map printStorage() {
        return storages.get(parameters.getStorageClassName()).printStorage();
    }

    private void delayForCardOperation(BillingStorage.CardOperation cardOperation){
        //long opCount = operationCount.incrementAndGet();
        if (!useDelay) return;
        //if (opCount % 50000 == 0) log.info("operation count delay called {} times, active Threads:{}", opCount, Thread.activeCount());
        int delay = delayInNanos[(int)(cardOperation.idCard() % delayInNanos.length)];
        try {
            Thread.sleep(0, delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
