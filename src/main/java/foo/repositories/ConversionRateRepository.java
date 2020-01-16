package foo.repositories;

import foo.models.ConversionRate;
import foo.util.Currencies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversionRateRepository extends JpaRepository<ConversionRate, Long> {
    Optional<ConversionRate> findByCurrency(Currencies.Currency currency);
}
