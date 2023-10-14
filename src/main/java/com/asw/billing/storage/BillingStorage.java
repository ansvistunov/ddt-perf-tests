package com.asw.billing.storage;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public interface BillingStorage {

   Card defaultCard = new Card(-1L,"xx", LocalDate.MIN,0.0);

   @Builder
   record Card(Long id, String persona, LocalDate createdAt, Double ballance){};

   @Builder
   record CardOperation(Long idCard, LocalDateTime operationDateTime, Double amount){};


   Card createCard(String persopa);
   void processOperation(CardOperation operation);
   Card getCard(Long id);
   Map printStorage();

}
