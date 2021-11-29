package ru.skillbox.diplom.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.mappers.FriendshipMapper;
import ru.skillbox.diplom.mappers.PersonMapper;
import ru.skillbox.diplom.model.Friendship;
import ru.skillbox.diplom.model.FriendshipResponseDto;
import ru.skillbox.diplom.model.FriendshipStatus;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.enums.FriendshipCode;
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
            friendsDtoResult.add(friendshipMapper.personToFriendship(friendship.getDstPerson(), friendship.getStatusId()));
        }
        for (Friendship friendship : friendships2) {
            friendsDtoResult.add(friendshipMapper.personToFriendship(friendship.getSrcPerson(), friendship.getStatusId()));
        }

//        List<FriendshipResponseDto> friendsDto1 = friendshipMapper.dstToFriendship(friendships1);
//        List<FriendshipResponseDto> friendsDto2 = friendshipMapper.srcToFriendship(friendships2);
//        List<FriendshipResponseDto> friendsDtoResult = Stream.concat(friendsDto1.stream(), friendsDto2.stream())
//                .distinct()
//                .collect(Collectors.toList());
        FriendshipResponse response = new FriendshipResponse();
        response.setOffset(offset);
        response.setPerPage(itemPerPage);
        response.setTotal(friendsDtoResult.size());
        response.setData(friendsDtoResult);
        log.info("finish searchFriends");

        return response;
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
        if (friendshipFromCurrentUser.isPresent()) {
            throw new BadRequestException(String.format("Friendship between %s and %s has already been requested",
                    currentUser.getEmail(), personToMakeFriend.getEmail()));
        }
        // никаких запросов от personToMakeFriend ранее не было - мы запрашиваем дружбу с ним
        else if (friendshipFromAnotherUser.isEmpty()) {
            makeNewFriendship(currentUser, personToMakeFriend);
        }
        // personToMakeFriend уже запрашивал дружбу - мы принимаем дружбу
        else if (friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.REQUEST)) {
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

    private void makeNewFriendship(Person currentUser, Person personToMakeFriend) {
        log.info("start makeNewFriend between {} and {}", currentUser.getId(), personToMakeFriend.getId());
        Friendship friendship = new Friendship();
        friendship.setSrcPerson(currentUser);
        friendship.setDstPerson(personToMakeFriend);
        FriendshipStatus status = friendshipStatusService.getFriendshipStatus(FriendshipCode.REQUEST);
        friendship.setStatusId(status);
        friendshipRepository.save(friendship);
        log.info("finish makeNewFriend between {} and {}", currentUser.getId(), personToMakeFriend.getId());
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

        if (friendshipFromCurrentUser.get().getStatusId().getCode().equals(FriendshipCode.REQUEST)) {
            // текущий пользователь запрашивал дружбу и отменил запрос
            // Удаляем из таблицы, чтобы ничего не висело на странице
            log.info("Delete request to make friendship between {} and {}",
                    currentUser.getEmail(), personToDelete.getEmail());

            friendshipRepository.delete(friendshipFromCurrentUser.get().getId());
        } else if (friendshipFromCurrentUser.get().getStatusId().getCode().equals(FriendshipCode.FRIEND)
                || friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.FRIEND)) {
            // если текущий стутас FRIEND и не важно кто был инициатором, блокируем друга
            log.info("User {} blocks user {}", currentUser.getEmail(), personToDelete.getEmail());

            FriendshipStatus friendship = friendshipStatusService.getFriendshipStatus(FriendshipCode.BLOCKED);
            friendshipFromCurrentUser.get().setStatusId(friendship);
            friendshipRepository.save(friendshipFromCurrentUser.get());
        } else if (friendshipFromAnotherUser.get().getStatusId().getCode().equals(FriendshipCode.REQUEST)) {
            // отменяем запрос на дружбу
            log.info("User {} declines friendship request from {}", currentUser.getEmail(), personToDelete.getEmail());
            FriendshipStatus newFriendshipStatus = friendshipStatusService.getFriendshipStatus(FriendshipCode.DECLINED);
            friendshipFromAnotherUser.get().setStatusId(newFriendshipStatus);
            friendshipRepository.save(friendshipFromAnotherUser.get());
        }

        return new MakeFriendResponse("ok");
    }

    public FriendshipResponse searchRecommendations(Person currentUser, int offset, int itemPerPage) {
        log.info("start searchRecommendations");

        // make it recursively with depth as a parameter
        // TODO think how to limit result by (offset, itemPerPage) considering two queries
        List<Friendship> friendsOfFriendsForward = friendshipRepository.findFriendsOfFriendsForward(currentUser.getId(),
                PageRequest.of(offset, itemPerPage));
        List<Friendship> friendsOfFriendsReverse = friendshipRepository.findFriendsOfFriendsReverse(currentUser.getId(),
                PageRequest.of(offset, itemPerPage));

        friendsOfFriendsForward.addAll(friendsOfFriendsReverse);

        List<FriendshipResponseDto> friendsOfFriendsDto = friendshipMapper.dstToFriendship(friendsOfFriendsForward);
        FriendshipResponse response = new FriendshipResponse();
        response.setOffset(offset);
        response.setPerPage(itemPerPage);
        response.setTotal(friendsOfFriendsDto.size());
        response.setData(friendsOfFriendsDto);
        log.info("finish searchRecommendations");

        return response;
    }
}
