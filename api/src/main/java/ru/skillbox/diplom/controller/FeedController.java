package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@RequestMapping("api/v1/feeds")
@PreAuthorize("hasAuthority('developers:read')")
public interface FeedController {

    @GetMapping
    ResponseEntity<?> getFeeds(@RequestParam (required = false) String name,
                            @RequestParam (defaultValue = "0") Integer offset,
                            @RequestParam (defaultValue = "20") Integer itemPerPage);
}
