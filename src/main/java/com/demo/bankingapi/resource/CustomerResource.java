package com.demo.bankingapi.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResource {

    private Long customerNumber;

    @NotEmpty
    private String name;

    @Email
    @NotEmpty
    private String email;

    @Pattern(regexp = "^([+]\\d{2})?\\d{10,11}$")
    @NotEmpty
    private String mobile;

    @Builder.Default
    private List<AccountResource> accounts = new ArrayList<>();

    private String status;
}
