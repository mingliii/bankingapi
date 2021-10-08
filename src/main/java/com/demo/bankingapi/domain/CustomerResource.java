package com.demo.bankingapi.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CustomerResource {

    private Long customerNumber;

    private String firstName;

    private String lastName;

    private List<AccountResource> accounts;

    private String status;
}
