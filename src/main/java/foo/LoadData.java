package foo;

import foo.models.Account;
import foo.models.ConversionRate;
import foo.repositories.AccountRepository;
import foo.repositories.ConversionRateRepository;
import foo.util.Currencies;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class LoadData {
    @Bean
    CommandLineRunner initAccountDataBase(AccountRepository accountRepository) {
        return args -> {
            accountRepository.save(new Account("test account 1", Currencies.Currency.EUR, new BigDecimal("2353.33")));
            accountRepository.save(new Account("test account 2", Currencies.Currency.USD, new BigDecimal("999.99")));
        };
    }

    @Bean
    CommandLineRunner initConversionsDataBase(ConversionRateRepository conversionRateRepository) {
        return args -> {
            conversionRateRepository.save(new ConversionRate(Currencies.Currency.USD, new BigDecimal("1.111345")));
        };
    }
}
