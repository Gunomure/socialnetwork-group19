package ru.skillbox.diplom.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.diplom.model.UserDto;
import ru.skillbox.diplom.model.enums.NotificationTypes;
import ru.skillbox.diplom.model.response.NotificationEmailDto;

import java.util.Collections;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SendingService {

    @Value("${values.twilio.phoneNumber}")
    private String PHONE_NUMBER;
    @Value("${values.twilio.enableTwilio}")
    private boolean enable_twilio;
    @Value("${email_service.path}")
    private String EMAIL_SERVICE_PATH;

    RestTemplate restTemplate = new RestTemplate();

    public void sendEmail(String to, NotificationTypes type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        NotificationEmailDto notificationEmailDto = new NotificationEmailDto(to, createMessageByType(type));
        HttpEntity<NotificationEmailDto> request = new HttpEntity<>(notificationEmailDto, headers);
        restTemplate.postForEntity(EMAIL_SERVICE_PATH + "/notifications", request, NotificationEmailDto.class);
    }

    public void sendPhoneMessage(String userNumber, NotificationTypes type) {
        if (enable_twilio) {
            Message message = Message.creator(new PhoneNumber(userNumber), new PhoneNumber(PHONE_NUMBER), createMessageByType(type)).create();
        }
    }

    private String createMessageByType(NotificationTypes type) {
        return String.format("Notification type: %s", type.toString().toLowerCase(Locale.ROOT));
    }
}
