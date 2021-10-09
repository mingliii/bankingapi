package com.demo.bankingapi.entity;

import java.util.Arrays;

public enum Currency {
    GBP("£"),
    USD(("$"));

    private String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    public static Currency from(String value) {
        return Arrays.stream(Currency.values())
                .filter(val -> val.name().equalsIgnoreCase(value))
                .findFirst()
                .orElse(GBP);
    }

    public String getSymbol() {
        return symbol;
    }
}
