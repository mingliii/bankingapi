package com.demo.bankingapi.resource;

import com.demo.bankingapi.entity.Currency;
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

    private Currency currency;

    /**
     * When used to create an account, the balance means the initial deposit into the account.
     */
    private BigDecimal balance;
}
