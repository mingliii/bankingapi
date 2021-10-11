package com.demo.bankingapi.resource;

import com.demo.bankingapi.entity.Currency;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class AccountResource {

    private Long accountNumber;

    @NotNull
    private Long customerNumber;

    private String status;

    private String type;

    @Builder.Default
    private Currency currency = Currency.GBP;

    /**
     * When used to create an account, the balance means the initial deposit into the account.
     */
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;
}
