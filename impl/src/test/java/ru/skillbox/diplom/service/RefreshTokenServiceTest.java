package ru.skillbox.diplom.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.skillbox.diplom.model.RefreshToken;
import ru.skillbox.diplom.repository.RefreshTokenRepository;
import ru.skillbox.diplom.repository.UserRepository;

import javax.naming.NamingException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RefreshTokenServiceTest {

    String testUserEmail = "javaprogroup19@gmail.com";
    String testUserPassword = "12345678";

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LdapService ldapService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Before
    public void setUp() throws Exception {
        ldapService.newConnection();
        ldapService.addUser(testUserEmail, testUserPassword);
    }

    @Test
    public void findByToken() throws NamingException {
        Assertions.assertEquals("3f1d8738-3c3a-4d85-ada3-abefcdf2a461",
                refreshTokenService.findByToken("3f1d8738-3c3a-4d85-ada3-abefcdf2a461").get().getToken());
    }

    @After
    public void tearDown() throws Exception {
        ldapService.deleteUser(testUserEmail);
    }
}