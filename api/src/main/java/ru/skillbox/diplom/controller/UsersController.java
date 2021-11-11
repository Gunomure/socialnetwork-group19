package ru.skillbox.diplom.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.response.UsersSearchResponse;

@CrossOrigin
@RequestMapping("/api/v1/users")
//@PreAuthorize("hasAuthority('developers:read')")
public interface UsersController {

    @GetMapping("/me")
    CommonResponse<PersonDto> getProfile();

    @GetMapping("/search")
    UsersSearchResponse searchUsers(@RequestParam(name = "first_name", required = false) String firstName,
                                    @RequestParam(name = "last_name", required = false) String lastName,
                                    @RequestParam(name = "age_from", defaultValue = "0", required = false) int ageFrom,
                                    @RequestParam(name = "age_to", defaultValue = "200", required = false) int ageTo,
                                    @RequestParam(required = false) String country,
                                    @RequestParam(required = false) String city,
                                    @RequestParam(defaultValue = "0", required = false) int offset,
                                    @RequestParam(defaultValue = "10", required = false) int itemPerPage);
}
