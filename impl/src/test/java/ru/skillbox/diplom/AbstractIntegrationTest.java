package ru.skillbox.diplom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skillbox.diplom.config.DbConfig;
import ru.skillbox.diplom.model.request.LoginRequest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = {
        DbConfig.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ActiveProfiles("local")
public abstract class AbstractIntegrationTest {
    /**
     * Web application context.
     */
    @Autowired
    protected WebApplicationContext ctx;

    /**
     * Mock mvc.
     */
    protected MockMvc mockMvc;

    /**
     * Object mapper. To convert object to json
     */
    @Autowired
    protected ObjectMapper mapper;

//    @Mock
//    Principal principal;

    /**
     * Create mock mvc.
     */
    @BeforeEach
    void initEach() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .build();
    }

    void loginAsUser() throws Exception {
        LoginRequest loginData = new LoginRequest("javaprogroup19@gmail.com", "password");
        String loginJson = mapper.writeValueAsString(loginData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        ).andDo(print())
                .andExpect(status().isOk());
    }

    void loginAsModerator() throws Exception {
        LoginRequest loginData = new LoginRequest();
        loginData.setEmail("moder-zerone@mail.ru");
        loginData.setPassword("Redomaccount1");
        String loginJson = mapper.writeValueAsString(loginData);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
        ).andDo(print())
                .andExpect(status().isOk());
    }

    void logout() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk());
    }
}