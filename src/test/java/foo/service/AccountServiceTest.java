package foo.service;

import foo.models.Account;
import foo.models.Payment;
import foo.repositories.AccountRepository;
import foo.repositories.ConversionRateRepository;
import foo.util.Currencies;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountServiceTest {
    @LocalServerPort
    private
    int randomServerPort;

    @Autowired
    private
    AccountRepository accountRepository;

    @Autowired
    private ConversionRateRepository conversionRateRepository;

    private HttpHeaders headers = new HttpHeaders();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void init() {
        accountRepository.deleteAll();
    }

    @Test
    public void accountCreationWithValidParameters_shouldPass() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/accounts/";
        URI uri = new URI(baseUrl);
        Account account = new Account("jaina", Currencies.Currency.EUR, new BigDecimal(100));

        HttpEntity<Account> request = new HttpEntity<>(account, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

        assertThat(200).isEqualTo(result.getStatusCodeValue());
    }

    @Test
    public void accountCreationWithExistingName_shouldNotPass() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/accounts/";
        URI uri = new URI(baseUrl);
        Account account = new Account("bob", Currencies.Currency.EUR, new BigDecimal(100));

        expectedEx.expect(HttpClientErrorException.class);
        expectedEx.expectMessage("already exists");

        HttpEntity<Account> request = new HttpEntity<>(account, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
        result = restTemplate.postForEntity(uri, request, String.class);
    }

    @Test
    public void paymentWithSameCurrencyWithValidParameters_shouldPass() throws Exception {
        long peterId = accountRepository.save(new Account("peter", Currencies.Currency.EUR, new BigDecimal(1000))).getId();
        long ciriId = accountRepository.save(new Account("ciri", Currencies.Currency.EUR, new BigDecimal(0))).getId();
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/payment/";
        URI uri = new URI(baseUrl);

        HttpEntity<Payment> request = new HttpEntity<>(new Payment(peterId, ciriId, new BigDecimal(1000)), headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

        assertThat(200).isEqualTo(result.getStatusCodeValue());
        assertThat(accountRepository.findByName("peter").get().getBalance().toString()).isEqualTo("0.00");
        assertThat(accountRepository.findByName("ciri").get().getBalance().toString()).isEqualTo("1000.00");
    }

    @Test
    public void paymentWithDifferentCurrenciesWithValidParameters_shouldPass() throws Exception {
        long jonesId = accountRepository.save(new Account("jones", Currencies.Currency.EUR, new BigDecimal(1000))).getId();
        long redId = accountRepository.save(new Account("red", Currencies.Currency.USD, new BigDecimal(0))).getId();

        BigDecimal rate = conversionRateRepository.findByCurrency(Currencies.Currency.USD).get().getConversionRateToEur();

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/payment/";
        URI uri = new URI(baseUrl);

        HttpEntity<Payment> request = new HttpEntity<>(new Payment(jonesId, redId, new BigDecimal(1000)), headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);

        String expectedBalance = new BigDecimal(1000).multiply(rate).setScale(2, RoundingMode.FLOOR).toString();

        assertThat(200).isEqualTo(result.getStatusCodeValue());
        assertThat(accountRepository.findByName("jones").get().getBalance().toString()).isEqualTo("0.00");
        assertThat(accountRepository.findByName("red").get().getBalance().toString()).isEqualTo(expectedBalance);
    }

    @Test
    public void paymentWithAmountOfZero_shouldFail() throws Exception {
        long richId = accountRepository.save(new Account("rich", Currencies.Currency.EUR, new BigDecimal(1000))).getId();
        long manId = accountRepository.save(new Account("man", Currencies.Currency.USD, new BigDecimal(0))).getId();

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/payment/";
        URI uri = new URI(baseUrl);

        expectedEx.expect(HttpClientErrorException.class);
        expectedEx.expectMessage("Payment amount must be greater than 0");

        HttpEntity<Payment> request = new HttpEntity<>(new Payment(richId, manId, new BigDecimal(0)), headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
    }

    @Test
    public void paymentWithNegativeAmount_shouldFail() throws Exception {
        long richId = accountRepository.save(new Account("rich", Currencies.Currency.EUR, new BigDecimal(1000))).getId();
        long manId = accountRepository.save(new Account("man", Currencies.Currency.USD, new BigDecimal(0))).getId();

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/payment/";
        URI uri = new URI(baseUrl);

        expectedEx.expect(HttpClientErrorException.class);
        expectedEx.expectMessage("Payment amount must be greater than 0");

        HttpEntity<Payment> request = new HttpEntity<>(new Payment(richId, manId, new BigDecimal(-5)), headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
    }

    @Test
    public void paymentWithInsufficientBalance_shouldFail() throws Exception {
        long tedId = accountRepository.save(new Account("ted", Currencies.Currency.EUR, new BigDecimal(5))).getId();
        long talkId = accountRepository.save(new Account("talk", Currencies.Currency.USD, new BigDecimal(0))).getId();

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/payment/";
        URI uri = new URI(baseUrl);

        expectedEx.expect(HttpClientErrorException.class);
        expectedEx.expectMessage("insufficient balance");

        HttpEntity<Payment> request = new HttpEntity<>(new Payment(tedId, talkId, new BigDecimal(100)), headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
    }

    @Test
    public void editingAccountWithValidInformation() throws URISyntaxException {
        Account kipAccount = accountRepository.save(new Account("kip", Currencies.Currency.EUR, new BigDecimal(0)));

        kipAccount.setBalance(new BigDecimal(100));
        kipAccount.setCurrency(Currencies.Currency.USD);
        kipAccount.setName("pin");

        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        restTemplate.setRequestFactory(requestFactory);

        final String baseUrl = "http://localhost:" + randomServerPort +"/api/accounts/";
        URI uri = new URI(baseUrl);

        HttpEntity<Account> request = new HttpEntity<>(kipAccount, headers);
        Account result = restTemplate.patchForObject(uri, request, Account.class);
        //assertThat(200).isEqualTo(result.getStatusCodeValue());
        assertThat(result.getId()).isEqualTo(kipAccount.getId());
        assertThat(result.getBalance().setScale(2, RoundingMode.FLOOR).toString()).isEqualTo("100.00");
        assertThat(result.getName()).isEqualTo("pin");
        assertThat(result.getCurrency()).isEqualTo(Currencies.Currency.USD);
    }

    @Test
    public void editingAccountWithInvalidInformation_existingName() throws Exception {
        Account kip1Account = accountRepository.save(new Account("kip1", Currencies.Currency.EUR, new BigDecimal(0)));
        accountRepository.save(new Account("kip2", Currencies.Currency.EUR, new BigDecimal(0)));

        kip1Account.setName("kip2");

        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        restTemplate.setRequestFactory(requestFactory);

        final String baseUrl = "http://localhost:" + randomServerPort +"/api/accounts/";
        URI uri = new URI(baseUrl);

        expectedEx.expect(HttpClientErrorException.class);
        expectedEx.expectMessage("Name already exists");

        HttpEntity<Account> request = new HttpEntity<>(kip1Account, headers);
        ResponseEntity<String> response = restTemplate.patchForObject(uri, kip1Account, ResponseEntity.class);
    }

    @Test
    public void deletingAccountThatExists() throws URISyntaxException {
        Account account = accountRepository.save(new Account("account", Currencies.Currency.EUR, new BigDecimal(10)));

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/accounts/" + account.getId();
        URI uri = new URI(baseUrl);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        restTemplate.delete(uri);
        assertThat(accountRepository.findById(account.getId()).isPresent()).isEqualTo(false);
    }

    @Test
    public void deletingAccountThatDoesNotExists() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:" + randomServerPort +"/api/accounts/" + 1;
        URI uri = new URI(baseUrl);

        expectedEx.expect(HttpClientErrorException.class);
        expectedEx.expectMessage("not found");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        restTemplate.delete(uri);
    }
}
