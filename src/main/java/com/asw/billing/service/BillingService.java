package com.asw.billing.service;

import com.asw.billing.common.Parameters;
import com.asw.billing.storage.BillingStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class BillingService {
    Parameters parameters;
    Map<String, BillingStorage> storages;

    public BillingStorage.Card createCard(String cardHolder){
        return storages.get(parameters.getStorageClassName()).createCard(cardHolder);
    }

    public boolean processOperation(BillingStorage.CardOperation operation){
        storages.get(parameters.getStorageClassName()).processOperation(operation);
        return true;
    }

    public boolean processOperations(List<BillingStorage.CardOperation> operations){
        BillingStorage storage = storages.get(parameters.getStorageClassName());
        for (BillingStorage.CardOperation operation: operations){
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

}
