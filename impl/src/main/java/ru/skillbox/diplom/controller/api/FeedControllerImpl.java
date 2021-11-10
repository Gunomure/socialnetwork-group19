package ru.skillbox.diplom.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.skillbox.diplom.controller.FeedController;
import ru.skillbox.diplom.service.FeedService;

@RestController
public class FeedControllerImpl implements FeedController {

    private final FeedService feedService;

    public FeedControllerImpl(FeedService feedService) {
        this.feedService = feedService;
    }

    @Override
    public ResponseEntity<?> getFeeds(String name, Integer offset, Integer itemPerPage) {
        return ResponseEntity.ok(feedService.getFeeds(name, offset, itemPerPage));
    }
}
