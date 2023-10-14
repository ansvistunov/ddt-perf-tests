package com.asw.billing.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DTOCardOperation (Long cardId, LocalDateTime operationDateTime, Double amount){
}
