package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.TagMapper;
import ru.skillbox.diplom.model.CommonResponse;
import ru.skillbox.diplom.model.Tag;
import ru.skillbox.diplom.model.TagDto;
import ru.skillbox.diplom.model.response.MessageResponse;
import ru.skillbox.diplom.model.response.TagListResponse;
import ru.skillbox.diplom.repository.TagRepository;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;

    private final TagMapper tagMapper = Mappers.getMapper(TagMapper.class);

    private final static Logger LOGGER = LogManager.getLogger(TagService.class);

    private final static String TAG_NOT_FOUND = "Tag doesn't exist";

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public TagListResponse getTags(String tagName, Integer offset, Integer itemPerPage){
        LOGGER.debug("start getTags: query = {}, offset = {}, item_per_page = {}", tagName, offset, itemPerPage);
        SpecificationUtil<Tag> spec = new SpecificationUtil<>();
        List<Tag> tags = tagRepository.findAll(Specification.where(spec.contains("tag", tagName)),
                PageRequest.of(offset, itemPerPage)).getContent();
        return tagMapper.convertToTagListResponse(offset, itemPerPage, tags);
    }

    public CommonResponse<TagDto> createTag(TagDto tagDto){
        LOGGER.debug("start createTag: tag_name = {}", tagDto.getTag());
        SpecificationUtil<Tag> spec = new SpecificationUtil<>();
        Optional<Tag> tagOptional = tagRepository.findOne(Specification.where(spec.contains("tag", tagDto.getTag())));
        if (tagOptional.isPresent()){
            return tagMapper.convertToCommonResponse(tagOptional.get());
        } else {
            Tag tag = new Tag(tagDto.getTag());
            tagRepository.save(tag);
            return tagMapper.convertToCommonResponse(tag);
        }
    }

    public CommonResponse<MessageResponse> deleteTag(Long id){
        LOGGER.debug("start deleteTag: tag_id = {}", id);
        Tag tag = tagRepository.findById(id).orElseThrow(() -> {
            LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + TAG_NOT_FOUND);
            throw new EntityNotFoundException(TAG_NOT_FOUND);
        });
        tagRepository.delete(tag);
        return tagMapper.convertToCommonResponse(new MessageResponse("ok"));
    }
}
