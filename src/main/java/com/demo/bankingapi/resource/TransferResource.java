package com.demo.bankingapi.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResource {

    @NotNull
    private Long fromAccountNumber;

    @NotNull
    private Long toAccountNumber;

    @Min(2)
    @Max(200)
    private BigDecimal amount;

    private String currency;
}
