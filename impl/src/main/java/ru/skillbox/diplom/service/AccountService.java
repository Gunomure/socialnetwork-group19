package ru.skillbox.diplom.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.control.MappingControl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.model.AccountNotifications;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.UserDto;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.enums.MessagePermission;
import ru.skillbox.diplom.model.enums.UserType;
import ru.skillbox.diplom.model.request.EmailChangeRequest;
import ru.skillbox.diplom.model.request.PasswordChangeRequest;
import ru.skillbox.diplom.model.request.AccountNotificationsBody;
import ru.skillbox.diplom.model.request.PasswordSetRequest;
import ru.skillbox.diplom.model.request.RegisterRequest;
import ru.skillbox.diplom.model.response.AccountNotificationsDto;
import ru.skillbox.diplom.repository.AccountNotificationRepository;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    @Value("${email_service.path}")
    private String EMAIL_SERVICE_PATH;
    @Value("${values.avatar.default}")
    private String DEFAULT;

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountNotificationRepository accountNotificationRepository;

    RestTemplate restTemplate = new RestTemplate();

    public void sendPasswordRecoveryEmail(String receiverEmail) {
        Person currentUser = personRepository.findByEmail(receiverEmail).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", receiverEmail)));
        UUID confirmationCode = UUID.randomUUID();
        currentUser.setConfirmationCode(confirmationCode.toString());
        personRepository.save(currentUser);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        UserDto user = new UserDto(currentUser.getEmail(), currentUser.getConfirmationCode());
        HttpEntity<UserDto> request = new HttpEntity<>(user, headers);
        restTemplate.postForEntity(EMAIL_SERVICE_PATH + "/password/recovery", request, UserDto.class);
    }

    public void setPassword(PasswordSetRequest passwordSetRequest) {
        Person currentUser = personRepository.findByConfirmationCode(passwordSetRequest.getToken()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User not found by confirmation code: %s", passwordSetRequest.getToken())));

        currentUser.setPassword(passwordEncoder.encode(passwordSetRequest.getPassword()));
        personRepository.save(currentUser);
    }

    public void changeEmail(EmailChangeRequest emailChangeRequest) {
        String email;
        try {
            email = getEmail();
        } catch (NullPointerException ex) {
            throw new EntityNotFoundException("Not authenticated");
        }
        Person currentUser = personRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User not found by email: %s", email)));

        currentUser.setEmail(emailChangeRequest.getNewEmail());
        personRepository.save(currentUser);
        SecurityContextHolder.clearContext();
    }

    public void changePassword(PasswordChangeRequest passwordChangeRequest) {
        String email;
        try {
            email = getEmail();
        } catch (NullPointerException ex) {
            throw new EntityNotFoundException("Not authenticated");
        }
        Person currentUser = personRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User not found by email: %s", email)));

        currentUser.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        personRepository.save(currentUser);
    }

    public void registerAccount(RegisterRequest registerRequest) {
        boolean personExists = personRepository.isExists(registerRequest.getEmail());
        if (!personExists) {
            Person user = new Person();
            user.setEmail(registerRequest.getEmail());
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setPassword(passwordEncoder.encode(registerRequest.getPasswd1()));
            user.setRegistrationDate(ZonedDateTime.now());
            user.setPermission(MessagePermission.ALL);
            user.setType(UserType.USER);
            user.setConfirmationCode(registerRequest.getCode());
            user.setLastOnlineTime(ZonedDateTime.now());
            user.setPhoto(DEFAULT);
            user.setBirthDate(TimeUtil.now().minusYears(30)); //TODO repair it!
            personRepository.save(user);

            AccountNotifications accountNotifications = new AccountNotifications();
            accountNotifications.setAuthorId(user);
            accountNotifications.setNewPost(true);
            accountNotifications.setPostComment(true);
            accountNotifications.setCommentComment(true);
            accountNotifications.setMessage(true);
            accountNotifications.setFriendRequest(true);
            accountNotifications.setFriendBirthday(true);
            accountNotifications.setSendEmailMessage(false);
            accountNotifications.setSendPhoneMessage(false);
            accountNotificationRepository.save(accountNotifications);

        } else {
            throw new BadRequestException(String.format("User with email %s already exists",
                    registerRequest.getEmail()));
        }
    }

    private String getEmail() throws NullPointerException {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public CommonResponse<?> getAccountNotifications() {
        Person person = getAuthenticatedUser();
        AccountNotifications notification = accountNotificationRepository.findByAuthorId(person);
        return getAccountNotificationsResponse(notification);
    }

    public CommonResponse<?> putAccountNotifications(AccountNotificationsBody body) {
        Person person = getAuthenticatedUser();
        AccountNotifications notification = accountNotificationRepository.findByAuthorId(person);
        switch (body.getNotificationType()) {
            case "POST_COMMENT":
                notification.setPostComment(body.getEnable());
                break;
            case "COMMENT_COMMENT":
                notification.setCommentComment(body.getEnable());
                break;
            case "FRIEND_REQUEST":
                notification.setFriendRequest(body.getEnable());
                break;
            case "MESSAGE":
                notification.setMessage(body.getEnable());
                break;
            case "FRIEND_BIRTHDAY":
                notification.setFriendBirthday(body.getEnable());
                break;
            case "NEW_POST":
                notification.setNewPost(body.getEnable());
                break;
        }
        accountNotificationRepository.save(notification);
        return getAccountNotificationsResponse(notification);
    }

    private CommonResponse<List<AccountNotificationsDto>> getAccountNotificationsResponse(AccountNotifications notification) {
        List<AccountNotificationsDto> dtoList = new ArrayList<>();

        dtoList.add(new AccountNotificationsDto("POST_COMMENT", notification.getPostComment()));
        dtoList.add(new AccountNotificationsDto("COMMENT_COMMENT", notification.getCommentComment()));
        dtoList.add(new AccountNotificationsDto("FRIEND_REQUEST", notification.getFriendRequest()));
        dtoList.add(new AccountNotificationsDto("MESSAGE", notification.getMessage()));
        dtoList.add(new AccountNotificationsDto("FRIEND_BIRTHDAY", notification.getFriendBirthday()));
        dtoList.add(new AccountNotificationsDto("NEW_POST", notification.getNewPost()));
        dtoList.add(new AccountNotificationsDto("SEND_EMAIL_MESSAGE", notification.getSendEmailMessage()));
        dtoList.add(new AccountNotificationsDto("SEND_PHONE_MESSAGE", notification.getSendPhoneMessage()));

        CommonResponse<List<AccountNotificationsDto>> response = new CommonResponse<>();
        response.setTimestamp(ZonedDateTime.now().toEpochSecond());
        response.setData(dtoList);
        return response;
    }

    public Person getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email)));
    }
}
