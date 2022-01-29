package ru.skillbox.diplom.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.*;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.enums.FriendshipCode;
import ru.skillbox.diplom.model.enums.NotificationTypes;
import ru.skillbox.diplom.model.request.LikeBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.CommentBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;
import ru.skillbox.diplom.model.response.FeedsResponse;
import ru.skillbox.diplom.model.response.MessageResponse;
import ru.skillbox.diplom.model.response.postResponse.CommentListResponse;
import ru.skillbox.diplom.repository.*;
import ru.skillbox.diplom.util.TimeUtil;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.skillbox.diplom.util.TimeUtil.getCurrentTimestampUtc;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final static String POST_ID_NOT_FOUND = "Post doesn't exist or blocked";
    private final static String COMMENT_ID_NOT_FOUND = "Comment doesn't exist or blocked";
    private final static String PARENT_COMMENT_ID_NOT_FOUND = "Parent comment doesn't exist or blocked";
    private final static String ERROR = "invalid_request";

    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    private final PostCommentMapper commentMapper = Mappers.getMapper(PostCommentMapper.class);
    private final ReportMapper reportMapper = Mappers.getMapper(ReportMapper.class);
    private final ResponseMapper feedsResponseMapper = Mappers.getMapper(ResponseMapper.class);
    private final PersonMapper personMapper = Mappers.getMapper(PersonMapper.class);

    private final NotificationService notificationService;
    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final ReportCommentRepository reportCommentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final FriendshipRepository friendshipRepository;
    private final TagRepository tagRepository;
    private final PostToTagRepository postToTagRepository;

    public FeedsResponse<List<PostDto>> getFeeds(String name, Integer offset, Integer itemPerPage){
        SpecificationUtil<Friendship> personSpec = new SpecificationUtil<>();
        String email = getAuthenticatedUser().getEmail();
        List<Person> subscriptions = friendshipRepository.findAll(
                        Specification.
                                where(personSpec.equals(    Friendship_.SRC_PERSON, Person_.EMAIL, email)
                                        .and(personSpec.equals(Friendship_.STATUS_ID, FriendshipStatus_.CODE, FriendshipCode.FRIEND)
                                                .or(personSpec.equals(Friendship_.STATUS_ID, FriendshipStatus_.CODE, FriendshipCode.SUBSCRIBED)))),
                        PageRequest.of(offset, itemPerPage))
                .getContent().stream().map(Friendship::getDstPerson).collect(Collectors.toList());
        SpecificationUtil<Post> spec = new SpecificationUtil<>();
        List<Post> feeds = postRepository.findAll(
                        Specification.
                                where(spec.contains(Post_.TITLE, name).or(spec.contains(Post_.POST_TEXT, name))).
                                and(spec.belongsToCollection(Post_.AUTHOR_ID, subscriptions).
                                                or(spec.equals(Post_.AUTHOR_ID, Person_.EMAIL, email))).
                                and(spec.equals(Post_.IS_BLOCKED, false)).
                                and(spec.between(Post_.TIME, null, ZonedDateTime.now())),
                        PageRequest.of(offset, itemPerPage, Sort.by("time").descending()))
                .getContent();
        FeedsResponse<List<PostDto>> response = feedsResponseMapper.convertToFeedsResponse(offset,itemPerPage);
        feedsResponseMapper.updateToFeedsResponse(feeds,response);
        if (!feeds.isEmpty()) {
            checkResponsePostLike(feeds, response.getData());
            createPostCommentDTOs(response.getData());
        }
        return response;
    }

    public CommonResponse<?> getPosts(String text, Long dateFrom, Long dateTo, String author, String tagQuery, Integer offset, Integer itemPerPage) {
        SpecificationUtil<Post> spec = new SpecificationUtil<>();
        Specification<Post> s1 = spec.between("time",
                TimeUtil.getZonedDateTimeFromMillis(dateFrom),
                TimeUtil.getZonedDateTimeFromMillis(dateTo));
        Specification<Post> s2 = spec.contains(Post_.TITLE, text);
        Specification<Post> s3 = spec.contains(Post_.POST_TEXT, text);
        Specification<Post> s4 = spec.equals(Post_.IS_BLOCKED, false);
        Specification<Post> s5 = spec.contains(Post_.AUTHOR_ID, Person_.FIRST_NAME, author);
        Specification<Post> s6 = spec.contains(Post_.AUTHOR_ID, Person_.LAST_NAME, author);
        String[] tags = tagQuery == null || tagQuery.isEmpty() ? null : tagQuery.split(",");
        Specification<Post> s7 = spec.containsTag(tags);

        List<Post> postList = postRepository.findAll(
                Specification.
                        where(s1).
                        and(s2.or(s3)).
                        and(s7).
                        and(s4).
                        and(s5.or(s6)),
                PageRequest.of(offset, itemPerPage, Sort.by("time").descending())).getContent().stream().distinct().collect(Collectors.toList());
        FeedsResponse<List<PostDto>> response = feedsResponseMapper.convertToFeedsResponse(offset,itemPerPage);
        feedsResponseMapper.updateToFeedsResponse(postList,response);
        if (!postList.isEmpty()) {
            checkResponsePostLike(postList, response.getData());
            createPostCommentDTOs(response.getData());
        }
        return response;
    }

    public CommonResponse<PersonDto> getPosts(Long id) {
        Person person = personRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", id)));
        PersonDto responseData = personMapper.toPersonDTO(person);
        CommonResponse<PersonDto> response = new CommonResponse<>();
        SpecificationUtil<Post> spec = new SpecificationUtil<>();
        List<Post> posts = postRepository.findAll(
                Specification.
                        where(spec.equals(Post_.IS_BLOCKED, false)).
                        and(spec.equals(Post_.AUTHOR_ID, Person_.EMAIL, person.getEmail())),
                Sort.by(Sort.Direction.DESC, "time"));
        List<PostDto> postsDtoList = postMapper.convertToListPostDto(posts);
        checkResponsePostLike(posts, postsDtoList);
        createPostCommentDTOs(postsDtoList);
        responseData.setPosts(postsDtoList);
        response.setTimestamp(getCurrentTimestampUtc());
        response.setData(responseData);
        return response;
    }

    public CommonResponse<?> getPostById(Long id) {
        Post post = postRepository.findByIdAndIsBlocked(id, false).orElseThrow(() -> {
            throw new EntityNotFoundException(POST_ID_NOT_FOUND);
        });
        CommonResponse<PostDto> response = postMapper.convertToCommonResponse(post);
        Person person = getAuthenticatedUser();
        response.getData().setMyLike(checkLike(post, person));
        createPostCommentDTOs(List.of(response.getData()));
        return response;
    }

    public CommonResponse<?> createPost(Long id, Long publishDate, PostBodyRequest body) {
        Person person = personRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", id)));
        ZonedDateTime time = publishDate != null ? TimeUtil.getZonedDateTimeFromMillis(publishDate) : ZonedDateTime.now();
        Post post = new Post();
        post.setAuthorId(person);
        post.setTitle(body.getTitle());
        post.setPostText(body.getPostText().replaceAll("<[^>]*>",""));
        post.setIsBlocked(false);
        post.setTime(time);
        SpecificationUtil<Tag> spec = new SpecificationUtil<>();
        postRepository.save(post);
        for (String tagName: body.getTags()){
            addTagToPost(tagName, post, spec);
        }
        log.info("!!!notification createPost person={} notification type={}", person.getFirstName(), NotificationTypes.POST);
        notificationService.createAllFriendNotification(person, NotificationTypes.POST, post.getId());
        CommonResponse<PostDto> response = postMapper.convertToCommonResponse(post);
        response.getData().setMyLike(checkLike(post, person));
        createPostCommentDTOs(Arrays.asList(response.getData()));
        return response;
    }

    public CommonResponse<?> editPost(Long id, Long publishDate, PostBodyRequest edit) {
        Post post = postRepository.findByIdAndIsBlocked(id, false).orElseThrow(() -> {
            throw new EntityNotFoundException(POST_ID_NOT_FOUND);
        });
        post.setTime(publishDate != null ? TimeUtil.getZonedDateTimeFromMillis(publishDate) : ZonedDateTime.now());
        post.setTitle(edit.getTitle());
        post.setPostText(edit.getPostText().replaceAll("<[^>]*>",""));
        List<String> oldTags = post.getPostToTags().stream().map((postToTag) -> postToTag.getTag().getTagName()).collect(Collectors.toList());
        SpecificationUtil<Tag> spec = new SpecificationUtil<>();
        for (String tag: edit.getTags()){
            if (!oldTags.contains(tag)){
                addTagToPost(tag, post, spec);
            }
        }
        for (String tag : oldTags){
            if (!Arrays.asList(edit.getTags()).contains(tag)){
                Optional<PostToTag> postToTag = postToTagRepository.findByTagTagNameAndPost(tag, post);
                if (postToTag.isPresent()) {
                    post.getPostToTags().remove(postToTag.get());
                    postToTagRepository.delete(postToTag.get());
                }
            }
        }
        Post savedPost = postRepository.save(post);
        CommonResponse<PostDto> response = postMapper.convertToCommonResponse(savedPost);
        Person person = getAuthenticatedUser();
        response.getData().setMyLike(checkLike(post, person));
        createPostCommentDTOs(Arrays.asList(response.getData()));
        return response;
    }

    public CommonResponse<?> deletePost(Long id) {
        Post post = postRepository.findByIdAndIsBlocked(id, false).orElseThrow(() -> {
            throw new EntityNotFoundException(POST_ID_NOT_FOUND);
        });
        post.setIsBlocked(true);
        Post savedPost = postRepository.save(post);
        CommonResponse<PostDto> response = postMapper.convertToCommonResponse(savedPost);
        Person person = getAuthenticatedUser();
        response.getData().setMyLike(checkLike(post, person));
        createPostCommentDTOs(Arrays.asList(response.getData()));
        return response;
    }

    public CommonResponse<List<PostCommentDto>> getCommentsById(Long postId, Integer offset, Integer itemPerPage) {
        List<PostComment> commentList = commentRepository.findByPostIdAndIsBlockedAndParentNull(postId, false).orElse(new ArrayList<>());
        CommentListResponse response = commentMapper.convertToPostCommentListResponse(offset, itemPerPage, commentList);
        recursiveCommentsDto(response.getData());
        return response;
    }

    public CommonResponse<PostCommentDto> createComment(Long postId, CommentBodyRequest body) {
        Post post = postRepository.findByIdAndIsBlocked(postId, false).orElseThrow(() -> {
            throw new EntityNotFoundException(POST_ID_NOT_FOUND);
        });
        Long parentId = body.getParentId();
        PostComment parentComment = null;
        if (parentId != null) {
            parentComment = commentRepository.findByIdAndIsBlocked(parentId, false).orElseThrow(() -> {
                throw new EntityNotFoundException(PARENT_COMMENT_ID_NOT_FOUND);
            });
            if (!post.getComments().contains(parentComment)) {
                throw new EntityNotFoundException(ERROR);
            }
        }
        Person person = getAuthenticatedUser();
        PostComment comment = commentMapper.convertToPostCommentEntity(post, person, parentComment, body.getCommentText());
        commentRepository.save(comment);
        log.info("!!!notification createComment person={} notification type={}", person.getFirstName(), NotificationTypes.POST_COMMENT);
        notificationService.createOnePersonNotification(post.getAuthorId(), comment.getAuthor(), NotificationTypes.POST_COMMENT, comment.getId());
        if (parentComment != null) {
            log.info("!!!notification createComment person={} notification type={}", parentComment.getAuthor(), NotificationTypes.COMMENT_COMMENT);
            notificationService.createOnePersonNotification(parentComment.getAuthor(), comment.getAuthor(), NotificationTypes.COMMENT_COMMENT, comment.getId());
        }

        CommonResponse<PostCommentDto> response = commentMapper.convertToCommonResponse(comment);
        recursiveCommentsDto(Arrays.asList(response.getData()));
        return response;
    }

    public CommonResponse<PostCommentDto> editComment(Long postId, Long commentId, CommentBodyRequest request) {
        PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, false, postId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                });
        comment.setCommentText(request.getCommentText());
        commentRepository.save(comment);
        CommonResponse<PostCommentDto> response = commentMapper.convertToCommonResponse(comment);
        recursiveCommentsDto(Arrays.asList(response.getData()));
        return response;
    }

    public CommonResponse<IdResponse> deleteComment(Long postId, Long commentId) {
        PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, false, postId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                });
        comment.setIsBlocked(true);
        commentRepository.save(comment);
        return commentMapper.convertToCommonResponse(new IdResponse(comment.getId()));
    }

    public CommonResponse<PostCommentDto> recoverComment(Long postId, Long commentId) {
        PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, true, postId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                });
        comment.setIsBlocked(false);
        commentRepository.save(comment);
        CommonResponse<PostCommentDto> response = commentMapper.convertToCommonResponse(comment);
        recursiveCommentsDto(Arrays.asList(response.getData()));
        return response;
    }

    public CommonResponse<MessageResponse> createCommentReport(Long postId, Long commentId){
        PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, false, postId)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                });
        CommentReport report = reportMapper.convertToCommentReportEntity(comment);
        reportCommentRepository.save(report);
        return reportMapper.convertToCommonResponse(new MessageResponse("ok"));
    }

    public CommonResponse<?> putLike(LikeBodyRequest body) {
        Person person = getAuthenticatedUser();
        Long postId;
        switch (body.getType()) {
            case "Post":
                postId = body.getItemId();
                Post post = postRepository.findByIdAndIsBlocked(postId, false).orElseThrow(() -> {
                    throw new EntityNotFoundException(POST_ID_NOT_FOUND);
                });
                PostLike postLike = postLikeRepository.findByPostIdAndPersonId(post, person).orElse(null);
                if (postLike == null) {
                    postLike = new PostLike();
                    postLike.setTime(ZonedDateTime.now());
                    postLike.setPostId(post);
                    postLike.setPersonId(person);
                    postLikeRepository.save(postLike);
                }
                break;
            case "Comment":
                postId = body.getPostId();
                Long commentId = body.getItemId();
                PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, false, postId)
                        .orElseThrow(() -> {
                            throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                        });
                CommentLike commentLike = commentLikeRepository.findByCommentIdAndPersonId(comment, person).orElse(null);
                if (commentLike == null) {
                    commentLike = new CommentLike();
                    commentLike.setTime(ZonedDateTime.now());
                    commentLike.setPersonId(person);
                    commentLike.setCommentId(comment);
                    commentLikeRepository.save(commentLike);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        CommonResponse<String> response = new CommonResponse<>();
        response.setData("ok");
        response.setTimestamp(ZonedDateTime.now().toEpochSecond());
        return response;
    }

    public CommonResponse<?> deleteLike(LikeBodyRequest body) {
        Person person = getAuthenticatedUser();
        switch (body.getType()) {
            case "Post":
                Post post = postRepository.findByIdAndIsBlocked(body.getItemId(), false).orElseThrow(() -> {
                    throw new EntityNotFoundException(POST_ID_NOT_FOUND);
                });
                postLikeRepository.findByPostIdAndPersonId(post, person).ifPresent(postLikeRepository::delete);
                break;
            case "Comment":
                PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(body.getItemId(), false, body.getPostId())
                        .orElseThrow(() -> {
                            throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                        });
                commentLikeRepository.findByCommentIdAndPersonId(comment, person).ifPresent(commentLikeRepository::delete);
                break;
            default:
                throw new IllegalArgumentException();
        }
        CommonResponse<String> response = new CommonResponse<>();
        response.setData("ok");
        response.setTimestamp(ZonedDateTime.now().toEpochSecond());
        return response;
    }

    public Person getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return personRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("User %s not found", email)));
    }

    private void createPostCommentDTOs(List<PostDto> posts){
        for (PostDto postDto: posts) {
            List<PostComment> commentList = commentRepository.findByPostIdAndIsBlockedAndParentNull(postDto.getId(), false).orElse(new ArrayList<>());
            List<PostCommentDto> commentDtoList = commentMapper.convertToPostCommentListDto(commentList);
            recursiveCommentsDto(commentDtoList);
            postDto.setComments(commentDtoList);
        }
    }

    private void recursiveCommentsDto(List<PostCommentDto> comments){
        if (!comments.isEmpty()) {
            Person person = getAuthenticatedUser();
            comments.forEach(c -> {
                List<PostComment> children = commentRepository
                        .findByParentIdAndIsBlockedAndPostId(c.getId(), false, c.getPostId())
                        .orElse(new ArrayList<>());
                List<Long> hasLike = new ArrayList<>();
                children.forEach(comment -> {
                    for (CommentLike like: comment.getLikes()) {
                        if (like.getPersonId().equals(person)) {
                            hasLike.add(comment.getId());
                            break;
                        }
                    }
                });
                List<PostCommentDto> postCommentDtos = commentMapper.convertToPostCommentListDto(children);
                postCommentDtos.stream().filter(commentDto -> hasLike.contains(commentDto.getId()))
                        .forEach(commentDto -> commentDto.setMyLike(true));
                recursiveCommentsDto(postCommentDtos);
                c.setComments(postCommentDtos);
            });
        }
    }

    private void checkResponsePostLike(List<Post> posts, List<PostDto> postDtoList){
        List<Long> hasLike = getUserPostLikes(posts);
        postDtoList.stream().filter(p -> hasLike.contains(p.getId()))
                .forEach(p -> p.setMyLike(true));
    }

    private List<Long> getUserPostLikes(List<Post> posts){
        List<Long> hasLike = new ArrayList<>();
        Person person = getAuthenticatedUser();
        posts.forEach(p -> {
                if (checkLike(p, person)) {
                    hasLike.add(p.getId());
                }
        });
        return hasLike;
    }

    private boolean checkLike(Post post, Person person) {
        List<PostLike> likes = post.getLikes();
        if (likes != null) {
            for (PostLike like : likes) {
                if (like.getPersonId().equals(person)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addTagToPost(String tag, Post post, SpecificationUtil<Tag> spec){
        Optional<Tag> tagOptional = tagRepository.findOne(Specification.where(spec.contains(Tag_.TAG_NAME, tag)));
        PostToTag postToTag = new PostToTag();
        if (tagOptional.isEmpty()){
            Tag newTag = new Tag(tag);
            tagRepository.save(newTag);
            postToTag.setTag(newTag);
        } else {
            postToTag.setTag(tagOptional.get());
        }
        postToTag.setPost(post);
        post.getPostToTags().add(postToTag);
        postToTagRepository.save(postToTag);
    }
}