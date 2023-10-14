package com.asw.billing.dto;

import java.time.LocalDateTime;


public record DTOCardOperation (Long cardId, LocalDateTime operationDateTime, Double amount){
}
