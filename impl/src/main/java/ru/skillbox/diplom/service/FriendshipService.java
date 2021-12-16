package ru.skillbox.diplom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.mappers.FriendshipMapper;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.enums.FriendshipCode;
import ru.skillbox.diplom.model.response.FriendshipCodeDto;
import ru.skillbox.diplom.model.response.FriendshipResponse;
import ru.skillbox.diplom.model.response.MakeFriendResponse;
import ru.skillbox.diplom.repository.FriendshipRepository;
import ru.skillbox.diplom.repository.FriendshipStatusRepository;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FriendshipService {

    private PersonRepository personRepository;
    private FriendshipRepository friendshipRepository;
    private PersonMapper personMapper;
    private FriendshipMapper friendshipMapper;
    private AuthService authService;
    private FriendshipStatusService friendshipStatusService;

    public FriendshipService(PersonRepository personRepository, FriendshipRepository friendshipRepository, PersonMapper personMapper, FriendshipMapper friendshipMapper, AuthService authService, FriendshipStatusRepository friendshipStatusRepository, FriendshipStatusService friendshipStatusService) {
        this.personRepository = personRepository;
        this.friendshipRepository = friendshipRepository;
        this.personMapper = personMapper;
        this.friendshipMapper = friendshipMapper;
        this.authService = authService;
        this.friendshipStatusService = friendshipStatusService;
    }

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

        //TODO make it better!
        List<Friendship> friendships1 = friendshipRepository.findAll(
                Specification.
                        where(s1.and(s2.or(s3))),
                PageRequest.of(offset / 2, itemPerPage / 2))
                .getContent();

        List<Friendship> friendships2 = friendshipRepository.findAll(
                Specification.
                        where(s4.and(s5.or(s6))),
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
                () -> new BadRequestException(String.format("User with id=%d nor found", id))
        );
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("Friendship cannot be formed");
        }

        Optional<Friendship> friendshipFromAnotherUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(personToMakeFriend.getId(), currentUser.getId());
        Optional<Friendship> friendshipFromCurrentUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(currentUser.getId(), personToMakeFriend.getId());

        // мы уже отправляли запрос - сообщаем пользователю что уже есть запрос
        if (friendshipFromCurrentUser.isPresent() &&
                friendshipFromCurrentUser.get().getStatusId().getCode().equals(FriendshipCode.FRIEND)) {
            throw new BadRequestException(String.format("Friendship between %s and %s has already been requested",
                    currentUser.getEmail(), personToMakeFriend.getEmail()));
        } else if (friendshipFromCurrentUser.isPresent() &&
                friendshipFromCurrentUser.get().getStatusId().getCode().equals(FriendshipCode.DECLINED)) {
            log.info("Update friendship from {} to {} between {} and {}",
                    friendshipFromCurrentUser.get().getStatusId(), FriendshipCode.REQUEST, currentUser.getEmail(), personToMakeFriend.getEmail());

            friendshipFromCurrentUser.get().setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.FRIEND));
            friendshipRepository.save(friendshipFromCurrentUser.get());
        } else if (friendshipFromCurrentUser.isPresent() &&
                friendshipFromCurrentUser.get().getStatusId().getCode().equals(FriendshipCode.SUBSCRIBED)) {
            friendshipFromCurrentUser.get().setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.REQUEST));
            friendshipRepository.save(friendshipFromCurrentUser.get());
        }
        // никаких запросов от personToMakeFriend ранее не было - мы запрашиваем дружбу с ним
        else if (friendshipFromAnotherUser.isEmpty()) {
            makeNewFriendship(currentUser, personToMakeFriend, FriendshipCode.REQUEST);
        }
        // personToMakeFriend уже запрашивал дружбу - мы принимаем дружбу
        else if (friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.REQUEST) ||
                friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.SUBSCRIBED)) {
            log.info("Update friendship from {} to {} between {} and {}",
                    friendshipFromAnotherUser.get().getStatusId(), FriendshipCode.FRIEND, currentUser.getEmail(),
                    personToMakeFriend.getEmail());

            friendshipFromAnotherUser.get().setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.FRIEND));
            friendshipRepository.save(friendshipFromAnotherUser.get());
        } else if (friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.DECLINED)) {
            // отклоняли запрос на дружбу, но теперь передумали и приняли запрос на дружбу
            friendshipFromAnotherUser.get().setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.FRIEND));
            friendshipRepository.save(friendshipFromAnotherUser.get());
        } else if (friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.BLOCKED)) {
            log.info("Update friendship from {} to {} between {} and {}",
                    friendshipFromAnotherUser.get().getStatusId(), FriendshipCode.REQUEST, currentUser.getEmail(), personToMakeFriend.getEmail());

            friendshipFromAnotherUser.get().setStatusId(friendshipStatusService.getFriendshipStatus(FriendshipCode.FRIEND));
            friendshipRepository.save(friendshipFromAnotherUser.get());
        } else {
            throw new BadRequestException(String.format("Got error while making friendship between %s and %s",
                    currentUser.getEmail(), personToMakeFriend.getEmail()));
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
        log.info("finish makeNewFriend between {} and {}", currentUser.getId(), personToMakeFriendship.getId());
    }

    public MakeFriendResponse subscribe(Person currentUser, Long id) {
        log.info("start subscribe: currentUser={}, id={}", currentUser, id);
        Person personToSubscribe = personRepository.findById(id).orElseThrow(
                () -> new BadRequestException(String.format("User with id=%d nor found", id))
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
                () -> new BadRequestException(String.format("User with id=%d nor found", id))
        );
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("Friendship cannot be formed");
        }

        Optional<Friendship> friendshipFromCurrentUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(currentUser.getId(), personToDelete.getId());
        Optional<Friendship> friendshipFromAnotherUser = friendshipRepository
                .findBySrcPersonIdAndDstPersonId(personToDelete.getId(), currentUser.getId());

        if (friendshipFromCurrentUser.isPresent() &&
                (friendshipFromCurrentUser.get().getStatusId().getCode().equals(FriendshipCode.REQUEST) ||
                        friendshipFromCurrentUser.get().getStatusId().getCode().equals(FriendshipCode.SUBSCRIBED))) {
            // текущий пользователь запрашивал дружбу и отменил запрос
            // Удаляем из таблицы, чтобы ничего не висело на странице
            log.info("Delete request to make friendship between {} and {}",
                    currentUser.getEmail(), personToDelete.getEmail());

            friendshipRepository.delete(friendshipFromCurrentUser.get().getId());
        } else if (friendshipFromCurrentUser.isPresent() &&
                friendshipFromCurrentUser.get().getStatusId().getCode().equals(FriendshipCode.FRIEND)) {
            // если текущий стутас FRIEND и не текущий пользователь был инициатором, блокируем друга
            log.info("User {} blocks user {}", currentUser.getEmail(), personToDelete.getEmail());

            FriendshipStatus friendship = friendshipStatusService.getFriendshipStatus(FriendshipCode.BLOCKED);
            friendshipFromCurrentUser.get().setStatusId(friendship);
            friendshipRepository.save(friendshipFromCurrentUser.get());
        } else if (friendshipFromAnotherUser.isPresent() &&
                friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.FRIEND)) {
            // если текущий стутас FRIEND и другой пользователь был инициатором, блокируем друга
            log.info("User {} blocks user {}", currentUser.getEmail(), personToDelete.getEmail());

            FriendshipStatus friendship = friendshipStatusService.getFriendshipStatus(FriendshipCode.BLOCKED);
            friendshipFromAnotherUser.get().setStatusId(friendship);
            friendshipRepository.save(friendshipFromAnotherUser.get());
        } else if (friendshipFromAnotherUser.isPresent() &&
                friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.REQUEST)) {
            // другой пользователь запрашивал дружбу - отменяем запрос на дружбу
            log.info("User {} declines friendship request from {}", currentUser.getEmail(), personToDelete.getEmail());
            FriendshipStatus newFriendshipStatus = friendshipStatusService.getFriendshipStatus(FriendshipCode.DECLINED);
            friendshipFromAnotherUser.get().setStatusId(newFriendshipStatus);
            friendshipRepository.save(friendshipFromAnotherUser.get());
        } else {
            throw new BadRequestException(String.format("Got error while DELETE %s to %s",
                    currentUser.getEmail(), personToDelete.getEmail()));
        }

        return new MakeFriendResponse("ok");
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
