package com.demo.bankingapi.resource;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResource {

    private Long id;

    private Long accountNumber;

    private String type;

    private BigDecimal inAmount;

    private BigDecimal outAmount;

    private BigDecimal balance;

    private String currency;

    private String description;

    private LocalDateTime createdAt;
}
