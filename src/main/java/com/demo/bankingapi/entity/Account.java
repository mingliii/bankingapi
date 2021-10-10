package com.demo.bankingapi.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "account_number")
    private Long accountNumber;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type = Type.DEBIT;

    @ManyToOne
    @JoinColumn(name = "customer_number", nullable = false)
    private Customer customer;

    @Column(name = "balance")
    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO;

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
        Account account = (Account) o;
        return accountNumber != null && Objects.equals(accountNumber, account.accountNumber);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public enum Status {
        ACTIVE,
        INACTIVE,
        FROZEN;

        public static Status from(String value) {
            return Arrays.stream(Status.values())
                    .filter(val -> val.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(ACTIVE);
        }
    }

    public enum Type {
        DEBIT, // default
        SAVING,
        CREDIT;

        public static Type from(String value) {
            return Arrays.stream(Type.values())
                    .filter(val -> val.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(DEBIT);
        }
    }
}
