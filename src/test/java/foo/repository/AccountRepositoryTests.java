package foo.repository;

import foo.models.Account;
import foo.repositories.AccountRepository;
import foo.util.Currencies;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository repository;

    @Test
    public void addingAccountWithCorrectData_shouldSucceed() {
        Account account = new Account("peter", Currencies.Currency.EUR, new BigDecimal(100));
        assertThat(entityManager.persistAndFlush(account)).isNotNull();
    }

    @Test
    public void whenFindByName_thenReturnAccount() {
        Account account = new Account("peter", Currencies.Currency.EUR, new BigDecimal(100));
        entityManager.persistAndFlush(account);

        Optional<Account> foundO = repository.findByName(account.getName());
        assertThat(foundO.isPresent()).isEqualTo(true);

        Account found = foundO.get();

        assertThat(found.getId()).isEqualTo(account.getId());
        assertThat(found.getName()).isEqualTo(account.getName());
        assertThat(found.getBalance()).isEqualTo(account.getBalance());
        assertThat(found.getCurrency()).isEqualTo(account.getCurrency());
    }

    @Test
    public void updatingAccountWithValidData() {
        Account account1 = new Account("peter", Currencies.Currency.EUR, new BigDecimal(100));
        entityManager.persistAndFlush(account1);
        assertThat(repository.findByName("peter").get().getId()).isEqualTo(1L);
        Account account2 = new Account(1L,"jones", Currencies.Currency.USD, new BigDecimal(200));
        repository.save(account2);

        Account result = repository.findById(1L).get();

        assertThat(result.getId()).isEqualTo(account2.getId());
        assertThat(result.getName()).isEqualTo(account2.getName());
        assertThat(result.getCurrency()).isEqualTo(account2.getCurrency());
        assertThat(result.getBalance()).isEqualTo(account2.getBalance());
    }
}
