package com.demo.bankingapi.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

    @Id
    @SequenceGenerator(name="customer_number_generator", sequenceName = "customer_number_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_number_generator")
    @Column(name = "customer_number")
    private Long customerNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Account> accounts = new ArrayList<>();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return customerNumber != null && Objects.equals(customerNumber, customer.customerNumber);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public List<Account> getAccounts() {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }

        return accounts;
    }

    public void addAccount(Account account) {
        account.setCustomer(this);
        getAccounts().add(account);
    }

    public void setAccounts(List<Account> accounts) {
        accounts.forEach(account -> account.setCustomer(this));
        this.accounts = accounts;
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        FROZEN;

        public static Status from(String value) {
            if (value == null) {
                return null;
            }

            return Arrays.stream(Status.values())
                    .filter(val -> val.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(ACTIVE);
        }
    }
}
