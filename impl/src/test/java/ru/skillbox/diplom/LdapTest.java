package ru.skillbox.diplom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.model.request.RegisterRequest;
import ru.skillbox.diplom.repository.UserRepository;
import ru.skillbox.diplom.service.AccountService;
import ru.skillbox.diplom.service.LdapService;

import javax.annotation.Resource;
import javax.naming.NamingException;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = LiquibaseAutoConfiguration.class)
public class LdapTest {

    @Autowired
    LdapService ldapService;
    @Autowired
    AccountService accountService;
    @Autowired
    UserRepository userRepository;

    String testUserEmail = "testJavaprogroup19@gmail.com";
    String testUserPassword = "12345678";

    @Before
    public void setUp() {
        LdapService.newConnection();
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(testUserEmail);
        registerRequest.setPasswd1(testUserPassword);
        registerRequest.setFirstName("testUserFirstName");
        registerRequest.setLastName("testUserLastName");
        accountService.registerAccount(registerRequest);
    }

    @Test
    public void testAuthUser() {
        Assertions. assertTrue(LdapService.authUser(testUserEmail, testUserPassword));
    }

    @Test
    public void testUpdateUserField() throws NamingException {
        ldapService.updateUserField(testUserEmail, "refreshToken", "88888888");
        Assertions.assertTrue(ldapService.searchUserField("refreshToken", "88888888"));
    }

    @Test
    public void testSearchUserField() throws NamingException {
        Assertions.assertTrue(ldapService.searchUserField("cn", testUserEmail));
    }

    @Test
    public void testSearchRefreshToken() throws NamingException {
        ldapService.updateUserField(testUserEmail, "refreshToken", "898989");
        RefreshToken refreshToken = ldapService.searchRefreshToken("898989");
        Assertions.assertEquals("898989", refreshToken.getToken());
        Assertions.assertEquals(testUserEmail, refreshToken.getUser().getEmail());
        Assertions.assertEquals("1990-06-14T20:16:28.280425Z", refreshToken.getExpiryDate().toString());
    }

    @After
    public void tearDown() throws Exception {
        ldapService.deleteUser(testUserEmail);
        userRepository.deleteById(
                userRepository.findByEmail(testUserEmail).get().getId());
    }
}
