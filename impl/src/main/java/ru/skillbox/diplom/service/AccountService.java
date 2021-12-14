package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.enums.MessagePermission;
import ru.skillbox.diplom.model.enums.UserType;
import ru.skillbox.diplom.model.request.PasswordSetRequest;
import ru.skillbox.diplom.model.request.RegisterRequest;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.TimeUtil;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AccountService {
    private final static Logger LOGGER = LogManager.getLogger(AccountService.class);

    @Value("${group19.passwordRecoveryPath}")
    private String PASSWORD_RECOVERY_PATH;
    @Value("${group19.frontendPort}")
    private int FRONTEND_PORT;
    @Value("${group19.websiteHost}")
    private String WEBSITE_HOST;

    private final JavaMailSender emailSender;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String FROM = "noreply@javaprogroup19.com";
    private static final String EMAIL_MESSAGE_SUBJECT = "Restore password";
    private final static String EMAIL_MESSAGE_TEMPLATE = "<a href=\"http://%s:%d/%s?token=%s\">Click to restore your password</a>";

    public AccountService(JavaMailSender emailSender, PersonRepository personRepository,
                          PasswordEncoder passwordEncoder) {
        this.emailSender = emailSender;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void sendPasswordRecoveryEmail(String receiverEmail) {
        Person currentUser = personRepository.findByEmail(receiverEmail).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", receiverEmail)));
        UUID confirmationCode = UUID.randomUUID();

        currentUser.setConfirmationCode(confirmationCode.toString());
        personRepository.save(currentUser);

        sendEmail(receiverEmail, String.format(EMAIL_MESSAGE_TEMPLATE,
                WEBSITE_HOST, FRONTEND_PORT, PASSWORD_RECOVERY_PATH, confirmationCode));
    }

    private void sendEmail(String to, String text) {
        LOGGER.info("start sendEmail: to={}, text={}", to, text);
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setText(text, true); //true for html type
            helper.setTo(to);
            helper.setSubject(EMAIL_MESSAGE_SUBJECT);
            helper.setFrom(new InternetAddress(FROM));// TODO sender email doesn't change for some reason
            emailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("Got error while sending email to {}", to, e);
        }
        LOGGER.info("finish sendEmail: to={}, text={}", to, text);
    }

    public void setPassword(PasswordSetRequest passwordSetRequest) {
        LOGGER.debug("setPassword: {}", passwordSetRequest);
        Person currentUser = personRepository.findByConfirmationCode(passwordSetRequest.getToken()).orElseThrow(
                () -> new EntityNotFoundException(String.format("User not found by confirmation code: %s", passwordSetRequest.getToken())));

        currentUser.setPassword(passwordEncoder.encode(passwordSetRequest.getPassword()));
        personRepository.save(currentUser);
    }

    public void registerAccount(RegisterRequest registerRequest) {
        LOGGER.info("start registerAccount: {}", registerRequest);
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
            user.setBirthDate(TimeUtil.now().minusYears(30)); //TODO repair it!
            personRepository.save(user);
        } else {
            throw new BadRequestException(String.format("User with email %s already exists",
                    registerRequest.getEmail()));
        }
        LOGGER.info("finish registerAccount: {}", registerRequest);
    }
}
