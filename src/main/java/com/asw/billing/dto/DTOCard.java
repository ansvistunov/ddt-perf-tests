package com.asw.billing.dto;

import java.time.LocalDate;

public record DTOCard(Long id, String holder, LocalDate createdAt, Double balance) {}
