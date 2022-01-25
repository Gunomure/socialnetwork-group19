package ru.skillbox.diplom.controller.api;

import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.controller.UsersController;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.Person;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.request.UpdateRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;
import ru.skillbox.diplom.model.response.MakeFriendResponse;
import ru.skillbox.diplom.model.response.UsersSearchResponse;
import ru.skillbox.diplom.service.AuthService;
import ru.skillbox.diplom.service.FriendshipService;
import ru.skillbox.diplom.service.PostService;
import ru.skillbox.diplom.service.UsersService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UsersControllerImpl implements UsersController {

    private final UsersService usersService;
    private final PostService postService;
    // temporary
    private final AuthService authService;
    private final FriendshipService friendshipService;

    public UsersControllerImpl(UsersService usersService, PostService postService, AuthService authService, FriendshipService friendshipService) {
        this.usersService = usersService;
        this.postService = postService;
        this.authService = authService;
        this.friendshipService = friendshipService;
    }

    @Override
    public CommonResponse<PersonDto> getProfile() {
        return usersService.getProfileData();
    }

    @Override
    public UsersSearchResponse searchUsers(String firstName, String lastName,
                                           int ageFrom, int ageTo,
                                           String country, String city,
                                           int offset, int itemPerPage) {
        return usersService.searchUsers(firstName, lastName, ageFrom, ageTo, country, city, offset, itemPerPage);
    }

    @Override
    public MakeFriendResponse block(HttpServletRequest request, Long id) {
        Person currentUser = authService.getCurrentUser(request);
        return friendshipService.blockFriend(currentUser, id);
    }

    @Override
    public CommonResponse<PersonDto> searchUser(@PathVariable Long id) {
        return usersService.searchUserById(id);
    }

    @Override
    public CommonResponse<PersonDto> updateProfile(@RequestBody UpdateRequest updateRequest) {
        return usersService.updateProfileData(updateRequest);
    }

    @Override
    public CommonResponse<?> createPost(Long id, Long date, PostBodyRequest body) {
        return postService.createPost(id, date, body);
    }

    @Override
    public CommonResponse<?> getPosts(Long id) {
        return postService.getPosts(id);
    }
}
