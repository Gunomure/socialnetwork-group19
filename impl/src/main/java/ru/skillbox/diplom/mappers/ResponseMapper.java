package ru.skillbox.diplom.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import ru.skillbox.diplom.mappers.converters.Converters;
import ru.skillbox.diplom.model.Post;
import ru.skillbox.diplom.model.PostDto;
import ru.skillbox.diplom.model.response.FeedsResponse;
import ru.skillbox.diplom.util.TimeUtil;

import java.util.List;

@Mapper(uses = {Converters.class, PersonMapper.class, PostMapper.class},
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ResponseMapper {

    @Mapping(target = "offset", source = "offset")
    @Mapping(target = "perPage", source = "perPage")
    public abstract FeedsResponse<List<PostDto>> convertToFeedsResponse(Integer offset, Integer perPage);

    public abstract List<PostDto> toListPostDto(List<Post> posts);

    public void updateToFeedsResponse(List<Post> list, FeedsResponse<List<PostDto>> response){
        response.setTotal(list.size());
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        if(list.size() == 0){
            response.setError("По указанному запросу не найдено новостей");
            return;
        }
        response.setData(toListPostDto(list));
    }
}
