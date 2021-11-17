package ru.skillbox.diplom.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.PersonDto;
import ru.skillbox.diplom.model.PostDto;
import ru.skillbox.diplom.model.request.UpdateRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;
import ru.skillbox.diplom.model.response.UsersSearchResponse;

@CrossOrigin
@RequestMapping("/api/v1/users")
@PreAuthorize("hasAuthority('developers:read')")
public interface UsersController {

    @GetMapping("/me")
    CommonResponse<PersonDto> getProfile();

    @PutMapping("/me")
    CommonResponse<PersonDto> updateProfile(@RequestBody UpdateRequest updateRequest);

    @GetMapping("/search")
    UsersSearchResponse searchUsers(@RequestParam(name = "first_name", required = false) String firstName,
                                    @RequestParam(name = "last_name", required = false) String lastName,
                                    @RequestParam(name = "age_from", defaultValue = "0", required = false) int ageFrom,
                                    @RequestParam(name = "age_to", defaultValue = "200", required = false) int ageTo,
                                    @RequestParam(required = false) String country,
                                    @RequestParam(required = false) String city,
                                    @RequestParam(defaultValue = "0", required = false) int offset,
                                    @RequestParam(defaultValue = "10", required = false) int itemPerPage);

    @GetMapping("/{id}")
    CommonResponse<PersonDto> searchUser(@PathVariable Long id);

    @PostMapping("/{id}/wall")
    CommonResponse<PersonDto> createPost(@PathVariable Long id,
                                         @RequestParam(name = "publish_date", required = false) Long date,
                                         @RequestBody PostBodyRequest body);
}
