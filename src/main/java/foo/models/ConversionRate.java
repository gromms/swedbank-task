package foo.models;

import foo.util.Currencies.Currency;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table
public class ConversionRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private Currency currency;

    @Column
    private String conversionRateToEur;

    public ConversionRate() {
    }

    public ConversionRate(Currency currency, BigDecimal conversionRateToEur) {
        this.currency = currency;
        this.conversionRateToEur = conversionRateToEur.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getConversionRateToEur() {
        return new BigDecimal(conversionRateToEur);
    }

    public void setConversionRateToEur(BigDecimal conversionRateToEur) {
        this.conversionRateToEur = conversionRateToEur.toString();
    }
}
