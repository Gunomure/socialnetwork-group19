package ru.skillbox.diplom.controller.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.controller.UsersController;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.request.UpdateRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;
import ru.skillbox.diplom.model.response.UsersSearchResponse;
import ru.skillbox.diplom.service.PostService;
import ru.skillbox.diplom.service.UsersService;

@RestController
public class UsersControllerImpl implements UsersController {

    private final UsersService usersService;
    private final PostService postService;

    public UsersControllerImpl(UsersService usersService, PostService postService) {
        this.usersService = usersService;
        this.postService = postService;
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
