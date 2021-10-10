package com.demo.bankingapi.resource;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@Builder
public class AccountResource {

    private Long accountNumber;

    @NotEmpty
    private Long customerNumber;

    private String status;

    private String type;

    private BigDecimal balance;
}
