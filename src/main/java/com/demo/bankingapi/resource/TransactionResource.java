package com.demo.bankingapi.resource;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.math.BigDecimal.ZERO;

@Data
@Builder
public class TransactionResource {

    private Long id;

    private Long accountNumber;

    private String type;

    @Builder.Default
    private BigDecimal inAmount = ZERO;

    @Builder.Default
    private BigDecimal outAmount = ZERO;

    @Builder.Default
    private BigDecimal balance = ZERO;

    private String currency;

    private String description;

    private LocalDateTime createdAt;
}
