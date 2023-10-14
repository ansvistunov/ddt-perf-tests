package com.asw.billing.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DTOCard(Long id, String holder, LocalDate createdAt, Double balance) {}
