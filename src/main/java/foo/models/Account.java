package foo.models;

import foo.util.Currencies.Currency;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(unique = true)
    @NotNull
    private String name;

    @Column
    @NotNull
    private Currency currency;

    @Column
    @NotNull
    private BigDecimal balance;

    public Account() {

    }

    public Account(String name, Currency currency, BigDecimal balance) {
        this.name = name;
        this.currency = currency;
        this.balance = balance;
    }

    public Account(Long id, String name, Currency currency, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal subtractBalance(BigDecimal amount) {
        return this.balance.subtract(amount);
    }

    public BigDecimal addBalance(BigDecimal amount) {
        return this.balance.add(amount);
    }
}
