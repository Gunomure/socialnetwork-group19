package ru.skillbox.diplom.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.mappers.FriendshipMapper;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.enums.FriendshipCode;
import ru.skillbox.diplom.model.enums.NotificationTypes;
import ru.skillbox.diplom.model.response.FriendshipCodeDto;
import ru.skillbox.diplom.model.response.FriendshipResponse;
import ru.skillbox.diplom.model.response.MakeFriendResponse;
import ru.skillbox.diplom.repository.FriendshipRepository;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendshipService {

    private final PersonRepository personRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendshipMapper friendshipMapper;
    private final FriendshipStatusService friendshipStatusService;
    private final NotificationService notificationService;

    public FriendshipResponse searchFriends(String currentUserEmail, String friendNameToSearch,
                                            Integer offset, Integer itemPerPage) {
        log.info("start searchFriends: name={}, offset={}, itemPerPage={}", friendNameToSearch, offset, itemPerPage);

        SpecificationUtil<Friendship> personSpec = new SpecificationUtil<>();
        // инициатором дружбы был srcPerson
        Specification<Friendship> s1 = personSpec.equals("srcPerson.email", currentUserEmail);
        Specification<Friendship> s2 = personSpec.contains("dstPerson.firstName", friendNameToSearch);
        Specification<Friendship> s3 = personSpec.contains("dstPerson.lastName", friendNameToSearch);
        // инициатором дружбы был dstPerson
        Specification<Friendship> s4 = personSpec.equals("dstPerson.email", currentUserEmail);
        Specification<Friendship> s5 = personSpec.contains("srcPerson.firstName", friendNameToSearch);
        Specification<Friendship> s6 = personSpec.contains("srcPerson.lastName", friendNameToSearch);
        // не показываем информацию, что кто-то нас заблокировал или подписался на нас
        Specification<Friendship> s7 = personSpec.notIn("statusId.code", List.of(FriendshipCode.BLOCKED, FriendshipCode.SUBSCRIBED));
        //TODO make it better!
        List<Friendship> friendships1 = friendshipRepository.findAll(
                Specification.
                        where(s1.and(s2.or(s3))),
                PageRequest.of(offset / 2, itemPerPage / 2))
                .getContent();

        List<Friendship> friendships2 = friendshipRepository.findAll(
                Specification.
                        where(s4.and(s5.or(s6)).and(s7)),
                PageRequest.of(offset / 2, itemPerPage / 2))
                .getContent();

        List<FriendshipResponseDto> friendsDtoResult = new ArrayList<>();
        for (Friendship friendship : friendships1) {
            friendsDtoResult.add(friendshipMapper.personToFriendship(friendship.getDstPerson(),
                    wrapFriendshipCode(friendship.getStatusId().getCode(), true)));
        }
        for (Friendship friendship : friendships2) {
            friendsDtoResult.add(friendshipMapper.personToFriendship(friendship.getSrcPerson(),
                    wrapFriendshipCode(friendship.getStatusId().getCode(), false)));
        }

        FriendshipResponse response = new FriendshipResponse();
        response.setOffset(offset);
        response.setPerPage(itemPerPage);
        response.setTotal(friendsDtoResult.size());
        response.setData(friendsDtoResult);
        log.info("finish searchFriends");

        return response;
    }

    private FriendshipCodeDto wrapFriendshipCode(FriendshipCode code, boolean request_to) {
        switch (code) {
            case REQUEST:
                return request_to ? FriendshipCodeDto.REQUEST_TO : FriendshipCodeDto.REQUEST_FROM;
            case FRIEND:
                return FriendshipCodeDto.FRIEND;
            case BLOCKED:
                return FriendshipCodeDto.BLOCKED;
            case DECLINED:
                return FriendshipCodeDto.DECLINED;
            case SUBSCRIBED:
                return FriendshipCodeDto.SUBSCRIBED;

            default:
                return null;
        }
    }

    public MakeFriendResponse makeFriend(Person currentUser, Long id) {
        log.info("start makeFriend: currentUser={}, id={}", currentUser, id);
        Person personToMakeFriend = personRepository.findById(id).orElseThrow(
                () -> new BadRequestException(String.format("User with id=%d not found", id))
        );
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("Friendship cannot be formed");
        }

        Optional<Friendship> friendshipFromAnotherUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(personToMakeFriend.getId(), currentUser.getId());
        Optional<Friendship> friendshipFromCurrentUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(currentUser.getId(), personToMakeFriend.getId());

        if (friendshipFromCurrentUser.isPresent()) {
            Friendship friendshipDirect = friendshipFromCurrentUser.get();
            switch (friendshipDirect.getStatusId().getCode()) {
                case DECLINED:
                    // мы отклоняли запрос на дружбу - теперь принимаем его
                    log.info("Update friendship from {} to {} between {} and {}",
                            friendshipDirect.getStatusId(), FriendshipCode.FRIEND, currentUser.getEmail(), personToMakeFriend.getEmail());

                    friendshipDirect.setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.FRIEND));
                    friendshipRepository.save(friendshipDirect);
                    break;
                case BLOCKED:
                case SUBSCRIBED:
                    // пользователь был заблокирован нами - создаем заново запрос на дружбу
                    log.info("Update friendship from {} to {} between {} and {}",
                            friendshipDirect.getStatusId(), FriendshipCode.REQUEST, currentUser.getEmail(), personToMakeFriend.getEmail());

                    friendshipDirect.setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.REQUEST));
                    friendshipRepository.save(friendshipDirect);
                    break;
                default:
                    throw new BadRequestException(String.format("Got error while making friendship between %s and %s",
                            currentUser.getEmail(), personToMakeFriend.getEmail()));
            }
        } else if (friendshipFromAnotherUser.isPresent()) {
            Friendship friendshipReverse = friendshipFromAnotherUser.get();
            switch (friendshipReverse.getStatusId().getCode()) {
                case REQUEST:
                    log.info("Update friendship from {} to {} between {} and {}",
                            friendshipReverse.getStatusId(), FriendshipCode.FRIEND, currentUser.getEmail(),
                            personToMakeFriend.getEmail());

                    friendshipReverse.setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.FRIEND));
                    friendshipRepository.save(friendshipReverse);
                    break;
                case SUBSCRIBED:
                    log.info("Update friendship from {} to {} between {} and {}",
                            friendshipReverse.getStatusId(), FriendshipCode.FRIEND, currentUser.getEmail(),
                            personToMakeFriend.getEmail());
                    // personToMakeFriend был подписан на нас - мы инициируем запрос в друзья
                    makeUserInitiator(friendshipReverse, currentUser);
                    friendshipReverse.setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.REQUEST));
                    friendshipRepository.save(friendshipReverse);
                default:
                    throw new BadRequestException(String.format("Got error while making friendship between %s and %s",
                            currentUser.getEmail(), personToMakeFriend.getEmail()));
            }
        } else {
            // никаких запросов от personToMakeFriend ранее не было - мы запрашиваем дружбу с ним
            makeNewFriendship(currentUser, personToMakeFriend, FriendshipCode.REQUEST);
        }

        return new MakeFriendResponse("ok");
    }

    private void makeNewFriendship(Person currentUser, Person personToMakeFriendship, FriendshipCode code) {
        log.info("start makeNewFriend between {} and {}", currentUser.getId(), personToMakeFriendship.getId());
        Friendship friendship = new Friendship();
        friendship.setSrcPerson(currentUser);
        friendship.setDstPerson(personToMakeFriendship);
        FriendshipStatus status = friendshipStatusService.getFriendshipStatus(code);
        friendship.setStatusId(status);
        friendshipRepository.save(friendship);
        log.info("!!!notification makeNewFriendship person={} notification type={}", personToMakeFriendship.getFirstName(), NotificationTypes.FRIEND_REQUEST);
        notificationService.createOnePersonNotification(personToMakeFriendship, currentUser, NotificationTypes.FRIEND_REQUEST, friendship.getId());
        log.info("finish makeNewFriend between {} and {}", currentUser.getId(), personToMakeFriendship.getId());
    }

    public MakeFriendResponse subscribe(Person currentUser, Long id) {
        log.info("start subscribe: currentUser={}, id={}", currentUser, id);
        Person personToSubscribe = personRepository.findById(id).orElseThrow(
                () -> new BadRequestException(String.format("User with id=%d not found", id))
        );
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("Cannot subscribe to yourself");
        }

        Optional<Friendship> friendshipFromAnotherUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(personToSubscribe.getId(), currentUser.getId());
        Optional<Friendship> friendshipFromCurrentUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(currentUser.getId(), personToSubscribe.getId());

        // если уже были отношения - подписываться нельзя
        if (friendshipFromAnotherUser.isPresent() || friendshipFromCurrentUser.isPresent()) {
            throw new BadRequestException("Relationship already exists");
        } else {
            makeNewFriendship(currentUser, personToSubscribe, FriendshipCode.SUBSCRIBED);
        }

        return new MakeFriendResponse("ok");
    }

    public MakeFriendResponse deleteFriend(Person currentUser, Long id) {
        log.info("start deleteFriend: currentUser={}, id={}", currentUser, id);
        Person personToDelete = personRepository.findById(id).orElseThrow(
                () -> new BadRequestException(String.format("User with id=%d not found", id))
        );
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("Friendship cannot be formed");
        }

        Optional<Friendship> friendshipFromCurrentUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(currentUser.getId(), personToDelete.getId());
        Optional<Friendship> friendshipFromAnotherUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(personToDelete.getId(), currentUser.getId());

        if (friendshipFromCurrentUser.isPresent()) {
            Friendship friendshipDirect = friendshipFromCurrentUser.get();
            switch (friendshipDirect.getStatusId().getCode()) {
                case REQUEST:
                case SUBSCRIBED:
                case FRIEND:
                    // текущий пользователь запрашивал дружбу/подписывался и отменил запрос
                    // Удаляем из таблицы, чтобы ничего не висело на странице
                    log.info("Delete relationship between {} and {}",
                            currentUser.getEmail(), personToDelete.getEmail());

                    friendshipRepository.delete(friendshipDirect.getId());
                    break;
            }
        } else if (friendshipFromAnotherUser.isPresent()) {
            Friendship friendshipReverse = friendshipFromAnotherUser.get();
            switch (friendshipReverse.getStatusId().getCode()) {
                case REQUEST:
                    // другой пользователь запрашивал дружбу - отменяем запрос на дружбу
                    log.info("User {} declines friendship request from {}", currentUser.getEmail(), personToDelete.getEmail());
                    FriendshipStatus newFriendshipStatus = friendshipStatusService.getFriendshipStatus(FriendshipCode.DECLINED);
                    makeUserInitiator(friendshipReverse, currentUser);
                    friendshipReverse.setStatusId(newFriendshipStatus);
                    friendshipRepository.save(friendshipReverse);
                    break;
                case FRIEND:
                    // между пользователями был статус FRIEND, удаляем отношения из таблицы
                    friendshipRepository.delete(friendshipReverse.getId());
                    break;
            }
        } else {
            throw new BadRequestException(String.format("Got error while DELETE %s to %s",
                    currentUser.getEmail(), personToDelete.getEmail()));
        }

        return new MakeFriendResponse("ok");
    }

    public MakeFriendResponse blockFriend(Person currentUser, Long id) {
        log.info("start blockFriend: currentUser={}, to block={}", currentUser, id);
        Person personToBlock = personRepository.findById(id).orElseThrow(
                () -> new BadRequestException(String.format("User with id=%d not found", id))
        );
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("Friendship cannot be formed");
        }
        Optional<Friendship> friendshipFromCurrentUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(currentUser.getId(), personToBlock.getId());
        Optional<Friendship> friendshipFromAnotherUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(personToBlock.getId(), currentUser.getId());

        if (friendshipFromCurrentUser.isPresent()) {
            Friendship friendshipDirect = friendshipFromCurrentUser.get();
            switch (friendshipDirect.getStatusId().getCode()) {
                // блокируем при любом текущем статусе
                case FRIEND:
                case SUBSCRIBED:
                case DECLINED:
                case REQUEST:
                    // если текущий статус FRIEND и текущий пользователь был инициатором, блокируем друга
                    log.info("User {} blocks user {}", currentUser.getEmail(), personToBlock.getEmail());

                    FriendshipStatus friendship = friendshipStatusService.getFriendshipStatus(FriendshipCode.BLOCKED);
                    makeUserInitiator(friendshipDirect, currentUser);
                    friendshipDirect.setStatusId(friendship);
                    friendshipRepository.save(friendshipDirect);
                    break;
                default:
                    throw new BadRequestException(String.format("Can not block user %s", personToBlock.getEmail()));
            }
        } else if (friendshipFromAnotherUser.isPresent()) {
            Friendship friendshipReverse = friendshipFromAnotherUser.get();
            switch (friendshipReverse.getStatusId().getCode()) {
                case FRIEND:
                    // если текущий статус FRIEND и другой пользователь был инициатором, блокируем друга
                    log.info("User {} blocks user {}", currentUser.getEmail(), personToBlock.getEmail());

                    FriendshipStatus friendship = friendshipStatusService.getFriendshipStatus(FriendshipCode.BLOCKED);
                    makeUserInitiator(friendshipReverse, currentUser);
                    friendshipReverse.setStatusId(friendship);
                    friendshipRepository.save(friendshipReverse);
                    break;
                default:
                    throw new BadRequestException(String.format("Can not block user %s", personToBlock.getEmail()));
            }
        } else {
            makeNewFriendship(currentUser, personToBlock, FriendshipCode.BLOCKED);
        }

        log.info("finish blockFriend: currentUser={}, to block={}", currentUser, id);

        return new MakeFriendResponse("ok");
    }

    private void makeUserInitiator(Friendship friendship, Person user) {
        if (friendship.getDstPerson().getId().equals(user.getId())) {
            friendship.setDstPerson(friendship.getSrcPerson());
            friendship.setSrcPerson(user);
        }
    }

    public FriendshipResponse searchRecommendations(Person currentUser, int offset, int itemPerPage) {
        log.info("start searchRecommendations");

        // make it recursively with depth as a parameter
        // TODO think how to limit result by (offset, itemPerPage) considering two queries
        List<Person> friendsOfFriendsForward = friendshipRepository.findFriendsOfFriendsForward(currentUser.getId(),
                PageRequest.of(offset, itemPerPage));
        List<Person> friendsOfFriendsReverse = friendshipRepository.findFriendsOfFriendsReverse(currentUser.getId(),
                PageRequest.of(offset, itemPerPage));

        friendsOfFriendsReverse.addAll(friendsOfFriendsForward);
        List<FriendshipResponseDto> responseData = new ArrayList<>();
        for (Person person : friendsOfFriendsReverse) {
            // FriendshipCode.FRIEND doesn't matter because we don't show status in recommendations
            responseData.add(friendshipMapper.personToFriendship(person, FriendshipCodeDto.FRIEND));
        }

        FriendshipResponse response = new FriendshipResponse();
        response.setOffset(offset);
        response.setPerPage(itemPerPage);
        response.setTotal(responseData.size());
        response.setData(responseData);
        log.info("finish searchRecommendations");

        return response;
    }
}
