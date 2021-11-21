package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.exception.EntityNotFoundException;
import ru.skillbox.diplom.mappers.PostCommentMapper;
import ru.skillbox.diplom.mappers.PostMapper;
import ru.skillbox.diplom.mappers.ReportMapper;
import ru.skillbox.diplom.mappers.ResponseMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.request.postRequest.CommentBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;
import ru.skillbox.diplom.model.response.MessageResponse;
import ru.skillbox.diplom.model.response.postResponse.CommonBadResponse;
import ru.skillbox.diplom.model.response.postResponse.PostListResponse;
import ru.skillbox.diplom.model.response.postResponse.PostResponse;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.repository.PostCommentRepository;
import ru.skillbox.diplom.repository.PostRepository;
import ru.skillbox.diplom.repository.ReportCommentRepository;
import ru.skillbox.diplom.util.TimeUtil;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final static Logger LOGGER = LogManager.getLogger(PostService.class);

    private final static String POST_ID_NOT_FOUND = "Post id not found";
    private final static String COMMENT_ID_NOT_FOUND = "Comment doesn't exist or blocked";
    private final static String PARENT_COMMENT_ID_NOT_FOUND = "Parent comment doesn't exist or blocked";
    private final static String ERROR = "invalid_request";

//    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    private final PostMapper mapper = Mappers.getMapper(PostMapper.class);
    private final PostCommentMapper commentMapper = Mappers.getMapper(PostCommentMapper.class);
    private final ResponseMapper responseMapper = Mappers.getMapper(ResponseMapper.class);
    private final ReportMapper reportMapper = Mappers.getMapper(ReportMapper.class);


    private final PostRepository postRepository;
    private final PersonRepository personRepository;
    private final PostCommentRepository commentRepository;
    private final ReportCommentRepository reportCommentRepository;

    public PostService(PostRepository postRepository,
                       PersonRepository personRepository,
                       PostCommentRepository commentRepository,
                       ReportCommentRepository reportCommentRepository) {
        this.postRepository = postRepository;
        this.personRepository = personRepository;
        this.commentRepository = commentRepository;
        this.reportCommentRepository = reportCommentRepository;
    }

    public ResponseEntity<?> getPosts(String text, Long dateFrom, Long dateTo, String author) { //String text, Long dateFrom, Long dateTo) {

        SpecificationUtil<Person> spec = new SpecificationUtil<>();
        Specification<Person> PersonS1 = spec.contains("firstName", author);
        Specification<Person> PersonS2 = spec.contains("lastName", author);
        List<Person> personList = personRepository.findAll(Specification.where(PersonS1).or(PersonS2));

        SpecificationUtil<Post> postSpec = new SpecificationUtil<>();
        Specification<Post> s1 = postSpec.between("time",
                TimeUtil.getZonedDateTimeFromMillis(dateFrom),
                TimeUtil.getZonedDateTimeFromMillis(dateTo));
        Specification<Post> s2 = postSpec.contains("title", text);
        Specification<Post> s3 = postSpec.contains("postText", text);
        Specification<Post> s4 = postSpec.equals("isBlocked", false);


        List<Post> postList = postRepository.findAll(Specification.where(s1).and(s2.or(s3)).and(s4));

        if (!personList.isEmpty()) {
            postList = postList.stream().filter(x -> personList.contains(x.getAuthorId())).collect(Collectors.toList());
        }

        List<PostDto> postDtoList = mapper.convertToListPostDto(postList);

        PostListResponse response = new PostListResponse();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setTotal(postDtoList.size());
        response.setData(postDtoList);
        return ResponseEntity.ok(response);
    }

    private PostResponse getResponseOk(Post post){
        PostDto postDto = mapper.convertToDto(post);
        PostResponse response = new PostResponse();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(postDto);
        return response;
    }

    private CommonBadResponse getResponseNotFound(){
        return new CommonBadResponse(ERROR, POST_ID_NOT_FOUND);
    }

    public ResponseEntity<?> getPostById(Long id) {
        LOGGER.info("start getPostById: {}", id);
        Post post = postRepository.findByIdAndIsBlocked(id, false).get();
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        return ResponseEntity.ok(getResponseOk(post));
    }

    public ResponseEntity<?> editPost(Long id, Long publishDate, PostBodyRequest edit) {
        LOGGER.info("start id = {}, publish date = {}, edit = {} ", id, publishDate, edit);
        Post post = postRepository.findByIdAndIsBlocked(id, false).get();
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        post.setTime(TimeUtil.getZonedDateTimeFromMillis(publishDate));
        post.setTitle(edit.getTitle());
        post.setPostText(edit.getPostText());
        postRepository.save(post);
        return ResponseEntity.ok(getResponseOk(post));
    }

    public ResponseEntity<?> deletePost(Long id) {
        LOGGER.info("start deletePost: {}", id);
        Post post = postRepository.findByIdAndIsBlocked(id, false).get();
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        post.setIsBlocked(true);
        postRepository.save(post);
        return ResponseEntity.ok(getResponseOk(post));
    }

    public CommonResponse<List<PostCommentDto>> getCommentsById(Long postId, Integer offset, Integer itemPerPage) {
        LOGGER.debug("start getCommentsById: post_id = {}", postId);
        SpecificationUtil<PostComment> specification = new SpecificationUtil<>();
        List<PostComment> commentList = commentRepository.findAll(
                Specification
                        .where(specification.equals("post", postId))
                        .and(specification.between("time", null, ZonedDateTime.now())),
                PageRequest.of(offset, itemPerPage, Sort.by("time").descending())).getContent();
        return commentMapper.convertToPostCommentListResponse(offset, itemPerPage, commentList);
    }

    public CommonResponse<PostCommentDto> createComment(Long postId, CommentBodyRequest body) {
        LOGGER.debug("start createComment: post_id = {}", postId);
        Post post = postRepository.findByIdAndIsBlocked(postId, false).orElseThrow(() -> {
            LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + POST_ID_NOT_FOUND);
            throw new EntityNotFoundException(POST_ID_NOT_FOUND);
        });
        Long parentId = body.getParentId();
        PostComment parentComment = null;
        if (parentId != null) {
            parentComment = commentRepository.findByIdAndIsBlocked(parentId, false).orElseThrow(() -> {
                LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + PARENT_COMMENT_ID_NOT_FOUND);
                throw new EntityNotFoundException(PARENT_COMMENT_ID_NOT_FOUND);
            });
            if (!post.getComments().contains(parentComment)) {
                LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + ERROR);
                throw new EntityNotFoundException(ERROR);
            }
        }
        PostComment comment = commentMapper.convertToPostCommentEntity(post, post.getAuthorId(), parentComment, body.getCommentText());
        commentRepository.save(comment);
        return commentMapper.convertToCommonResponse(comment);
    }

    public CommonResponse<PostCommentDto> editComment(Long postId, Long commentId, CommentBodyRequest request) {
        LOGGER.debug("start editComment: post_id = {}, comment_id = {}", postId, commentId);
        PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, false, postId)
                .orElseThrow(() -> {
                    LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + COMMENT_ID_NOT_FOUND);
                    throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                });
        if (request.getParentId() != null) {
            PostComment parentComment = commentRepository.findByIdAndIsBlockedAndPostId(request.getParentId(), false, postId)
                    .orElseThrow(() -> {
                        LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + PARENT_COMMENT_ID_NOT_FOUND);
                        throw new EntityNotFoundException(PARENT_COMMENT_ID_NOT_FOUND);
                    });
            comment.setParent(parentComment);
        } else comment.setParent(null);
        comment.setCommentText(request.getCommentText());
        commentRepository.save(comment);
        return commentMapper.convertToCommonResponse(comment);
    }

    public CommonResponse<IdResponse> deleteComment(Long postId, Long commentId) {
        LOGGER.debug("start deleteComment: postId = {}, commentId = {}", postId, commentId);
        PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, false, postId)
                .orElseThrow(() -> {
                    LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + COMMENT_ID_NOT_FOUND);
                    throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                });
        comment.setIsBlocked(true);
        commentRepository.save(comment);
        return commentMapper.convertToCommonResponse(new IdResponse(comment.getId()));
    }

    public CommonResponse<PostCommentDto> recoverComment(Long postId, Long commentId) {
        LOGGER.debug("start recoverComment: postId = {}, commentId = {}", postId, commentId);
        PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, true, postId)
                .orElseThrow(() -> {
                    LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + COMMENT_ID_NOT_FOUND);
                    throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                });
        comment.setIsBlocked(false);
        commentRepository.save(comment);
        return commentMapper.convertToCommonResponse(comment);
    }

    public CommonResponse<MessageResponse> createCommentReport(Long postId, Long commentId){
        LOGGER.debug("start createCommentReport: postId = {}, commentId = {}", postId, commentId);
        PostComment comment = commentRepository.findByIdAndIsBlockedAndPostId(commentId, false, postId)
                .orElseThrow(() -> {
                    LOGGER.error(EntityNotFoundException.class.getSimpleName() + ": " + COMMENT_ID_NOT_FOUND);
                    throw new EntityNotFoundException(COMMENT_ID_NOT_FOUND);
                });
        CommentReport report = reportMapper.convertToCommentReportEntity(comment);
        reportCommentRepository.save(report);
        return reportMapper.convertToCommonResponse(new MessageResponse("ok"));
    }

//    public ResponseEntity<?> recoverPost(Long id) {
//        LOGGER.info("start recoverPost: {}", id);
//        Post post = postRepository.findByIdAndIsBlocked(id, true);
//        if (post == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
//        }
//        post.setIsBlocked(false);
//        postRepository.save(post);
//        return ResponseEntity.ok(getResponseOk(post));
//    }

}