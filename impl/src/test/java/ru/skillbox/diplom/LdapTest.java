package ru.skillbox.diplom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.model.request.RegisterRequest;
import ru.skillbox.diplom.repository.UserRepository;
import ru.skillbox.diplom.service.AccountService;
import ru.skillbox.diplom.service.LdapService;

import javax.naming.NamingException;

//@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = LiquibaseAutoConfiguration.class)
public class LdapTest extends AbstractIntegrationTest {

    @Autowired
    LdapService ldapService;
    @Autowired
    AccountService accountService;
    @Autowired
    UserRepository userRepository;

    String testUserEmail = "testJavaprogroup19@gmail.com";
    String testUserPassword = "12345678";

    @BeforeEach
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
    public void testAuthUserTest() {
        Assertions.assertTrue(LdapService.authUser(testUserEmail, testUserPassword));
    }

    @Test
    public void testUpdateUserFieldTest() throws NamingException {
        ldapService.updateUserField(testUserEmail, "refreshToken", "88888888");
        Assertions.assertTrue(ldapService.searchUserField("refreshToken", "88888888"));
    }

    @Test
    public void testSearchUserFieldTest() throws NamingException {
        Assertions.assertTrue(ldapService.searchUserField("cn", testUserEmail));
    }

    @Test
    public void testSearchRefreshTokenTest() throws NamingException {
        ldapService.updateUserField(testUserEmail, "refreshToken", "898989");
        RefreshToken refreshToken = ldapService.searchRefreshToken("898989");
        Assertions.assertEquals("898989", refreshToken.getToken());
        Assertions.assertEquals(testUserEmail, refreshToken.getUser().getEmail());
        Assertions.assertEquals("1990-06-14T20:16:28.280425Z", refreshToken.getExpiryDate().toString());
    }

    @AfterEach
    public void tearDown() throws Exception {
        ldapService.deleteUser(testUserEmail);
        userRepository.deleteById(
                userRepository.findByEmail(testUserEmail).get().getId());
    }
}
