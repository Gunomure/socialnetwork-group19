package ru.skillbox.diplom.controller.api;

import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.UsersController;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.service.UserService;

@RestController
public class UsersControllerImpl implements UsersController {

    private final UserService userService;

    public UsersControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CommonResponse<PersonDto> getProfile() {
        return userService.getProfileData();
    }
}
