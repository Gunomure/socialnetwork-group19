package ru.skillbox.diplom.controller.api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.UsersController;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.response.UsersSearchResponse;
import ru.skillbox.diplom.service.UsersService;

@RestController
public class UsersControllerImpl implements UsersController {
    private final static Logger LOGGER = LogManager.getLogger(UsersControllerImpl.class);

    private final UsersService usersService;

    public UsersControllerImpl(UsersService usersService) {
        this.usersService = usersService;
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
        LOGGER.info("start searchUsers: firstName={}, lastName={}, " +
                        "ageFrom={}, ageTo={}, country={}, city={}, offset={}, itemPerPage={}",
                firstName, lastName, ageFrom, ageTo, country, city, offset, itemPerPage);
        return usersService.searchUsers(firstName, lastName, ageFrom, ageTo, country, city, offset, itemPerPage);
    }
}
