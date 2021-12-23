package ru.skillbox.diplom.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.FriendsController;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.response.FriendshipResponse;
import ru.skillbox.diplom.model.response.MakeFriendResponse;
import ru.skillbox.diplom.service.AuthService;
import ru.skillbox.diplom.service.FriendshipService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FriendsControllerImpl implements FriendsController {
    private FriendshipService friendshipService;
    private AuthService authService;

    public FriendsControllerImpl(FriendshipService friendshipService, AuthService authService) {
        this.friendshipService = friendshipService;
        this.authService = authService;
    }

    @Override
    public FriendshipResponse searchFriends(HttpServletRequest request, String name, Integer offset, Integer itemPerPage) {
        String currentUser = authService.getCurrentUser(request).getEmail();
        return friendshipService.searchFriends(currentUser, name, offset, itemPerPage);
    }

    @Override
    public MakeFriendResponse makeFriend(HttpServletRequest request, Long id) {
        Person currentUser = authService.getCurrentUser(request);
        return friendshipService.makeFriend(currentUser, id);
    }

    @Override
    public MakeFriendResponse deleteFriend(HttpServletRequest request, Long id) {
        Person currentUser = authService.getCurrentUser(request);
        return friendshipService.deleteFriend(currentUser, id);
    }

    @Override
    public MakeFriendResponse subscribe(HttpServletRequest request, @PathVariable Long id) {
        Person currentUser = authService.getCurrentUser(request);
        return friendshipService.subscribe(currentUser, id);
    }

    @Override
    public FriendshipResponse recommendations(HttpServletRequest request, Integer offset, Integer itemPerPage) {
        Person currentUser = authService.getCurrentUser(request);
        return friendshipService.searchRecommendations(currentUser, offset, itemPerPage);
    }
}
