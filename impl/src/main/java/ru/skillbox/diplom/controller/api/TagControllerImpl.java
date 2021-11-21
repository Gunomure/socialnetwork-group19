package ru.skillbox.diplom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.TagController;
import ru.skillbox.diplom.model.TagDto;
import ru.skillbox.diplom.service.TagService;

@RestController
public class TagControllerImpl implements TagController {

    private final TagService tagService;

    public TagControllerImpl(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    public ResponseEntity<?> getTags(@RequestParam String tag,
                                     @RequestParam (required = false, defaultValue = "0") Integer offset,
                                     @RequestParam (required = false, defaultValue = "10") Integer itemPerPage) {
        return ResponseEntity.ok(tagService.getTags(tag, offset, itemPerPage));
    }

    @Override
    public ResponseEntity<?> createTag(@RequestBody TagDto tagDto) {
        return ResponseEntity.ok(tagService.createTag(tagDto));
    }

    @Override
    public ResponseEntity<?> deleteTag(@RequestParam Long id) {
        return ResponseEntity.ok(tagService.deleteTag(id));
    }
}
