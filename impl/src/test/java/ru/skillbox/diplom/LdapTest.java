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
import ru.skillbox.diplom.repository.UserRepository;
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

    String testUserEmail = "javaprogroup19@gmail.com";
    String testUserPassword = "12345678";

    @Before
    public void setUp() {
        ldapService.newConnection();
        ldapService.addUser(testUserEmail, testUserPassword);
    }

    @Test
    public void testAuthUser() {
        Assertions.assertTrue(LdapService.authUser(testUserEmail, testUserPassword));
    }

    @Test
    public void testUpdateUserField() throws NamingException {
        ldapService.updateUserField(testUserEmail, "sn", "1");
        Assertions.assertTrue(ldapService.searchUserField("sn", "1"));
    }

    @Test
    public void testSearchUserField() throws NamingException {
        Assertions.assertTrue(ldapService.searchUserField("cn", testUserEmail));
    }

    @Test
    public void testSearchRefreshToken() throws NamingException {
        RefreshToken refreshToken = ldapService.searchRefreshToken("ab8232ea-d100-4205-b112-1515adc78ee6");
        Assertions.assertEquals("ab8232ea-d100-4205-b112-1515adc78ee6", refreshToken.getToken());
        Assertions.assertEquals(testUserEmail, refreshToken.getUser().getEmail());
        Assertions.assertEquals("1990-06-14T20:16:28.280425Z", refreshToken.getExpiryDate().toString());
    }

    @After
    public void tearDown() throws Exception {
        ldapService.deleteUser(testUserEmail);
    }
}
