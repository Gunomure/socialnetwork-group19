package ru.skillbox.diplom.controller;

import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.response.FriendshipResponse;
import ru.skillbox.diplom.model.response.MakeFriendResponse;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@RequestMapping("/api/v1/friends")
//@PreAuthorize("hasAuthority('developers:read')")
public interface FriendsController {

    @GetMapping
    FriendshipResponse searchFriends(HttpServletRequest request,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(defaultValue = "0") Integer offset,
                                     @RequestParam(defaultValue = "20") Integer itemPerPage);

    @PostMapping(value = "/{id}")
    MakeFriendResponse makeFriend(HttpServletRequest request, @PathVariable Long id);

    @DeleteMapping(value = "/{id}")
    MakeFriendResponse deleteFriend(HttpServletRequest request, @PathVariable Long id);

    @GetMapping("/recommendations")
    FriendshipResponse recommendations(HttpServletRequest request,
                                       @RequestParam(defaultValue = "0") Integer offset,
                                       @RequestParam(defaultValue = "20") Integer itemPerPage);
}
