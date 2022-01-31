package ru.skillbox.diplom.service;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.SocketIOClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.NotificationMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.enums.FriendshipCode;
import ru.skillbox.diplom.model.enums.NotificationTypes;
import ru.skillbox.diplom.repository.*;
import ru.skillbox.diplom.util.specification.SpecificationUtil;
import ru.skillbox.diplom.websocket.SocketIOService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipStatusRepository friendshipStatusRepository;
    private final NotificationMapper notificationMapper;
    private final SocketIOService socketIOService;
    private final PersonRepository personRepository;
    private final AccountNotificationRepository accountNotificationRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final MessageRepository messageRepository;

    private final SendingService sendingService;

    public CommonResponse<?> getNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person =  personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email)));
        List<Notification> notifications = notificationRepository.findByPersonIdAndWasSend(person.getId(), false).orElse(new ArrayList<>());
        CommonResponse<List<NotificationDto>> response = new CommonResponse<>();
        List<NotificationDto> notificationDTOs = notificationMapper.convertToListDto(notifications);
        for (NotificationDto dto: notificationDTOs) {
            NotificationType type = notificationTypeRepository.findById(dto.getTypeId()).get();
            dto.setType(type.getName().toString());
        }
        response.setTimestamp(ZonedDateTime.now().toEpochSecond());
        response.setData(notificationDTOs);
        return response;
    }

    public CommonResponse<?> changeStatus(Long id, String type) {

        NotificationType notificationType = notificationTypeRepository.findByName(NotificationTypes.valueOf(type));

        List<Notification> notifications = notificationRepository.findByPersonIdAndTypeId(id, notificationType.getId()).orElse(new ArrayList<>());
        setWasSend(notifications);
        CommonResponse<List<NotificationDto>> response = new CommonResponse<>();
        response.setTimestamp(ZonedDateTime.now().toEpochSecond());
        response.setData(notificationMapper.convertToListDto(notifications));
        return response;
    }

    public CommonResponse<?> changeAllStatus() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person =  personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email)));
        List<Notification> notifications = notificationRepository.findByPersonId(person.getId()).orElse(new ArrayList<>());
        setWasSend(notifications);
        CommonResponse<List<NotificationDto>> response = new CommonResponse<>();
        response.setTimestamp(ZonedDateTime.now().toEpochSecond());
        response.setData(notificationMapper.convertToListDto(notifications));
        return response;
    }

    private void setWasSend(List<Notification> notifications) {
        for (Notification n: notifications) {
            n.setWasSend(true);
            notificationRepository.save(n);
        }
    }

    public void createAllFriendNotification(Person person, NotificationTypes type, Long entityId) {
        for (Friendship f: getFriendshipById(person.getId(), "srcPerson")) {
            createOnePersonNotification(f.getDstPerson(), person, type, entityId);
        }
        for (Friendship f: getFriendshipById(person.getId(), "dstPerson")) {
            createOnePersonNotification(f.getSrcPerson(), person, type, entityId);
        }
    }

    private List<Friendship> getFriendshipById(Long id, String property) {
        FriendshipStatus statusFriend = friendshipStatusRepository.findByCode(FriendshipCode.FRIEND);
        FriendshipStatus statusSubscribed = friendshipStatusRepository.findByCode(FriendshipCode.SUBSCRIBED);

        SpecificationUtil<Friendship> personSpec = new SpecificationUtil<>();
        Specification<Friendship> s1 = personSpec.equals(property, id);
        Specification<Friendship> s2 = personSpec.equals("statusId", statusFriend.getId());
        Specification<Friendship> s3 = personSpec.equals("statusId", statusSubscribed.getId());
        return friendshipRepository.findAll(Specification.where(s1.and(s2.or(s3))));
    }

    public void createOnePersonNotification(Person personTo, Person personFrom, NotificationTypes type, Long entityId) {
        AccountNotifications accountNotifications = accountNotificationRepository.findByAuthorId(personTo);

        String content = "";
        boolean isEnable = false;
        switch (type) {
            case POST:
                isEnable = accountNotifications.getNewPost();
                Post post = postRepository.getById(entityId);
                content = post.getTitle();
                break;
            case POST_COMMENT:
                isEnable = accountNotifications.getPostComment();
                PostComment postComment = commentRepository.getById(entityId);
                content = postComment.getCommentText();
                break;
            case COMMENT_COMMENT:
                isEnable = accountNotifications.getCommentComment();
                PostComment commentComment = commentRepository.getById(entityId);
                content = commentComment.getCommentText();
                break;
            case FRIEND_REQUEST:
                isEnable = accountNotifications.getFriendRequest();
                content = "запрос на добавление в друзья";
                break;
            case MESSAGE:
                isEnable = accountNotifications.getMessage();
                Message message = messageRepository.getById(entityId);
                content = message.getMessageText();
                break;
            case BIRTHDAY:
                isEnable = accountNotifications.getFriendBirthday();
                content = "празднует день рождения";
                break;
        }
        if (isEnable) {
            log.info("!!!createOnePersonNotification isEnable person={} notification type={}", personTo.getFirstName(), type);
            NotificationType notificationType = notificationTypeRepository.findByName(type);
            Notification notification = new Notification();
            notification.setTypeId(notificationType.getId());
            ZonedDateTime time = ZonedDateTime.now();
            notification.setSentTime(time);
            notification.setPersonId(personTo.getId());
            notification.setEntityId(entityId);
            notification.setWasSend(false);
            notification.setContent(content);
            notification.setUserName(String.format("%s  %s", personFrom.getFirstName(), personFrom.getLastName()));
            notification.setPhoto(personFrom.getPhoto());
            if (accountNotifications.getSendEmailMessage()) {
                notification.setContact("email");
                sendingService.sendEmail(personTo.getEmail(), type);
            }
            if (accountNotifications.getSendPhoneMessage()) {
                String contact = "phone";
                if (notification.getContact() != null) {
                    contact = String.format("%s & %s", notification.getContact(), contact);
                }
                notification.setContact(contact);
                sendingService.sendPhoneMessage(personTo.getPhone(), type);
            }
            notificationRepository.save(notification);
            NotificationDto notificationDto = notificationMapper.convertToDto(notification);
            notificationDto.setType(type.toString());
            sendNotification(personTo.getId(), notificationDto);
        }
    }

    private void sendNotification(Long id, NotificationDto notificationDto) {
        log.info("!!!sendNotification personID={} notification type={}", id, notificationDto.getType());
        List<SocketIOClient> recipients = socketIOService.getClientStorage().getClients(id);
        if (recipients != null && !recipients.isEmpty()) {
            for (SocketIOClient recipient : recipients) {
                recipient.sendEvent("notification", new AckCallback<>(String.class) {
                    @Override
                    public void onSuccess(String result) {
                        Notification notification = notificationRepository.findById(Long.valueOf(result)).orElse(null);
                        if (notification != null) {
                            notification.setWasSend(true);
                            notificationRepository.save(notification);
                        }
                    }
                }, notificationDto);
            }
        }
    }



    @Scheduled(cron = "0 0 0 ? * *")
    public void sendBirthdayEvent() {
        List<Person> userList = personRepository.findAll();
        for (Person p: userList) {
            ZonedDateTime birthday = p.getBirthDate();
            ZonedDateTime today = ZonedDateTime.now();
            if (birthday.getMonth().equals(today.getMonth()) && birthday.getDayOfMonth() == today.getDayOfMonth()) {
                createAllFriendNotification(p, NotificationTypes.BIRTHDAY, p.getId());
            }
        }
    }


}
