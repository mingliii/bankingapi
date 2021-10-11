package com.demo.bankingapi.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

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
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_number")
    private Account account;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "in_amount")
    @Builder.Default
    private BigDecimal inAmount = BigDecimal.ZERO;

    @Column(name = "out_amount")
    @Builder.Default
    private BigDecimal outAmount = BigDecimal.ZERO;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transaction that = (Transaction) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public enum Type {
       // DIRECT_DEBIT, // not support yet
       // DEBIT_CARD, // not support yet
        DEPOSIT,
        TRANSFER;

        public static Type from(String value) {
            return Arrays.stream(Type.values())
                    .filter(val -> val.name().equalsIgnoreCase(value))
                    .findFirst()
                    .orElse(TRANSFER);
        }
    }
}
