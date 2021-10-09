package com.demo.bankingapi.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResource {

    private Long accountNumber;

    private Long customerNumber;

    private String status;

    private String type;

    private BigDecimal balance;
}
