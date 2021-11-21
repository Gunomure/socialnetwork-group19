package ru.skillbox.diplom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.diplom.model.TagDto;

@CrossOrigin
@RequestMapping("api/v1/tags")
//@PreAuthorize("hasAuthority('developers:read')")
public interface TagController {

    @GetMapping
    ResponseEntity<?> getTags(@RequestParam String tag,
                              @RequestParam (required = false, defaultValue = "0") Integer offset,
                              @RequestParam (required = false, defaultValue = "10") Integer itemPerPage);

    @PostMapping
    ResponseEntity<?> createTag(@RequestBody TagDto tagDto);

    @DeleteMapping
    ResponseEntity<?> deleteTag(@RequestParam Long id);
}
