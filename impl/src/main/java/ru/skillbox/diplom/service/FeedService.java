package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.BadRequestException;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.FeedMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.response.FeedsResponse;
import ru.skillbox.diplom.repository.PostRepository;
import ru.skillbox.diplom.util.TimeUtil;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class FeedService {

    private final static Logger LOGGER = LogManager.getLogger(FeedService.class);
    private final FeedMapper mapper = Mappers.getMapper(FeedMapper.class);

    private final PostRepository postRepository;

    public FeedService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public FeedsResponse<List<PostDto>> getFeeds(String name, Integer offset, Integer itemPerPage){
        LOGGER.info("getFeeds: text = {}, offset = {}, itemPerPage = {}", name, offset, itemPerPage);
        SpecificationUtil<Post> feedSpec = new SpecificationUtil<>();
        Specification<Post> s1 = feedSpec.between("time", null, ZonedDateTime.now());
        Specification<Post> s2 = feedSpec.contains("title", name);
        Specification<Post> s3 = feedSpec.contains("postText", name);
        Specification<Post> s4 = feedSpec.equals("isBlocked", false);
        List<Post> feeds = postRepository.findAll(
                Specification.
                        where(s2.or(s3)).
                        and(s4).
                        and(s1),
                        PageRequest.of(offset, itemPerPage))
                .getContent();
        FeedsResponse<List<PostDto>> response = new FeedsResponse<>();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setOffset(offset);
        response.setPerPage(itemPerPage);
        response.setTotal(feeds.size());
        if (feeds.isEmpty()) {
            response.setError("По указанному запросу не найдено новостей");
            return response;
        }
        List<PostDto> feedsDto = mapper.toDtoList(feeds);
        response.setData(feedsDto);
        response.setError("getting feed success");
        return response;
    }
}
