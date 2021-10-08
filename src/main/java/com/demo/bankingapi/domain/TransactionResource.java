package com.demo.bankingapi.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class TransactionResource {

    private Long accountNumber;

    private Date createdAt;

    private String type;

    private String status;

    private BigDecimal amount;

    private BigDecimal balance;

    private String description;
}
