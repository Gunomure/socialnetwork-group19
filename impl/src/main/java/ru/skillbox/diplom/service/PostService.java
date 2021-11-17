package ru.skillbox.diplom.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.factory.Mappers;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skillbox.diplom.mappers.PostCommentMapper;
import ru.skillbox.diplom.mappers.PostMapper;
import ru.skillbox.diplom.model.*;
import ru.skillbox.diplom.model.request.postRequest.CommentBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.PostBodyRequest;
import ru.skillbox.diplom.model.request.postRequest.PostRequest;
import ru.skillbox.diplom.model.response.postResponse.*;
import ru.skillbox.diplom.repository.PersonRepository;
import ru.skillbox.diplom.repository.PostCommentRepository;
import ru.skillbox.diplom.repository.PostRepository;
import ru.skillbox.diplom.util.TimeUtil;
import ru.skillbox.diplom.util.specification.SpecificationUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final static Logger LOGGER = LogManager.getLogger(LanguageService.class);

    private final static String POST_ID_NOT_FOUND = "Post id not found";
//    private final static String COMMENT_ID_NOT_FOUND = "Comment id not found";
    private final static String POST_COMMENT_NOT_FOUND = "Post comment not found ";
    private final static String ERROR = "invalid_request";

//    private final PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    private final PostMapper mapper = Mappers.getMapper(PostMapper.class);
    private final PostCommentMapper commentMapper =  Mappers.getMapper(PostCommentMapper.class);


    private final PostRepository postRepository;
    private final PersonRepository personRepository;
    private final PostCommentRepository commentRepository;
    private final CommentsService commentsService;

    public PostService(PostRepository postRepository, PersonRepository personRepository, PostCommentRepository commentRepository, CommentsService commentsService) {
        this.postRepository = postRepository;
        this.personRepository = personRepository;
        this.commentRepository = commentRepository;
        this.commentsService = commentsService;
    }

    public ResponseEntity<?> getPosts(String text, Long dateFrom, Long dateTo, String author){ //String text, Long dateFrom, Long dateTo) {

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
        Post post = postRepository.findByIdAndIsBlocked(id, false);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        return ResponseEntity.ok(getResponseOk(post));
    }

    public ResponseEntity<?> editPost(Long id, Long publishDate, PostBodyRequest edit) {
        LOGGER.info("start id = {}, publish date = {}, edit = {} ", id, publishDate, edit);
        Post post = postRepository.findByIdAndIsBlocked(id, false);
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
        Post post = postRepository.findByIdAndIsBlocked(id, false);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        post.setIsBlocked(true);
        postRepository.save(post);
        return ResponseEntity.ok(getResponseOk(post));
    }

    public ResponseEntity<?> getCommentsById(Long id, PostRequest request) {
        LOGGER.info("start getCommentsById: {}", id);
        Post post = postRepository.findByIdAndIsBlocked(id, false);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        List<PostComment> commentList = post.getComments()
                .stream().filter(x -> !x.getIsBlocked()).collect(Collectors.toList());
        List<PostCommentDto> commentsDto = commentMapper.toDtoPostComment(commentList);

        CommentListResponse response = new CommentListResponse();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(commentsDto);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> createComment(Long id, CommentBodyRequest body) {
        LOGGER.info("start createComment: {}", id);
        Post post = postRepository.findByIdAndIsBlocked(id, false);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        List<PostComment> comments = post.getComments();

        Long parentId = body.getId();
        PostComment parentComment = null;
        if (parentId != null) {
            parentComment = commentRepository.getById(parentId);
            if (!comments.contains(parentComment)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
            }
        }

        PostComment comment = commentsService.addComment(post.getAuthorId(), parentComment, post, body.getCommentText());
        PostCommentDto commentDto = commentMapper.toDtoPostComment(comment);
        CommentResponse response = new CommentResponse();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(commentDto);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> editComment(Long id, Long commentId, CommentBodyRequest body) {
        LOGGER.info("start editComment: {}", id);
        Post post = postRepository.findByIdAndIsBlocked(id, false);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        PostComment comment = commentRepository.findByIdAndIsBlocked(commentId, false);
        List<PostComment> comments = post.getComments();
        if (!comments.contains(comment)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CommonBadResponse(ERROR, POST_COMMENT_NOT_FOUND));
        }
        PostComment parentComment = commentRepository.getById(body.getId());
        comment.setCommentText(body.getCommentText());
        comment.setParentId(parentComment);

        PostComment savedComment = commentRepository.save(comment);
        PostCommentDto commentDto = commentMapper.toDtoPostComment(savedComment);

        CommentResponse response = new CommentResponse();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(commentDto);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> deleteComment(Long id, Long commentId) {
        LOGGER.info("start deleteComment postId = {}, commentId = {}", id, commentId);
        Post post = postRepository.findByIdAndIsBlocked(id, false);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        PostComment comment = commentRepository.findByIdAndIsBlocked(commentId, false);
        List<PostComment> comments = post.getComments();
        if (!comments.contains(comment)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CommonBadResponse(ERROR, POST_COMMENT_NOT_FOUND));
        }
        comment.setIsBlocked(true);
        PostComment savedComment = commentRepository.save(comment);
        PostCommentDto commentDto = commentMapper.toDtoPostComment(savedComment);

        CommentResponse response = new CommentResponse();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(commentDto);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> recoverComment(Long id, Long commentId) {
        LOGGER.info("start recoverComment postId = {}, commentId = {}", id, commentId);
        Post post = postRepository.findByIdAndIsBlocked(id, false);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getResponseNotFound());
        }
        PostComment comment = commentRepository.findByIdAndIsBlocked(commentId, true);
        List<PostComment> comments = post.getComments();
        if (!comments.contains(comment)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CommonBadResponse(ERROR, POST_COMMENT_NOT_FOUND));
        }
        comment.setIsBlocked(false);
        PostComment savedComment = commentRepository.save(comment);
        PostCommentDto commentDto = commentMapper.toDtoPostComment(savedComment);

        CommentResponse response = new CommentResponse();
        response.setTimestamp(TimeUtil.getCurrentTimestampUtc());
        response.setData(commentDto);

        return ResponseEntity.ok(response);
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