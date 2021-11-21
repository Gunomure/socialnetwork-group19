//package ru.skillbox.diplom;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import ru.skillbox.diplom.controller.api.PostControllerImpl;
//import ru.skillbox.diplom.model.CommonResponse;
//import ru.skillbox.diplom.model.PostCommentDto;
//import ru.skillbox.diplom.model.request.postRequest.CommentBodyRequest;
//import ru.skillbox.diplom.service.PostService;
//
//import java.nio.charset.StandardCharsets;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest(classes = PostControllerImpl.class)
//@AutoConfigureMockMvc
//@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
//public class TestPostController {
//
//    @MockBean
//    private PostService postService;
//
//    @Autowired
//    private MockMvc mvc;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    static CommentBodyRequest request;
//    static CommonResponse<PostCommentDto> response;
//    static PostCommentDto postCommentDto;
//
//    @BeforeAll
//    static void init(){
//        request = new CommentBodyRequest();
//        request.setCommentText("some text");
//        request.setParentId(78L);
//        postCommentDto = new PostCommentDto();
//        postCommentDto.setCommentText(request.getCommentText());
//        postCommentDto.setParentId(request.getParentId());
//        postCommentDto.setAuthorId(15L);
//        postCommentDto.setBlocked(false);
//        postCommentDto.setPostId(1L);
//        postCommentDto.setTime(5574184616845L);
//        response = new CommonResponse<>();
//        response.setData(postCommentDto);
//    }
//
//    @Test
//    public void createCommentAPI() throws Exception{
//        Mockito.when(postService.createComment((long) 1, request)).thenReturn(response);
//        mvc.perform(MockMvcRequestBuilders
//                .post("/api/v1/post/{id}/comments", 1)
//                .content(mapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON)
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("data.comment_text").value(request.getCommentText()))
//                .andExpect(jsonPath("data.post_id").value(1L))
//                .andExpect(jsonPath("data.parent_id").value(request.getParentId()))
//                .andExpect(jsonPath("data.author_id").value(15L))
//                .andExpect(jsonPath("data.is_blocked").value(false))
//                .andExpect(jsonPath("data.time").value(5574184616845L))
//                .andExpect(jsonPath("error").doesNotExist())
//                .andDo(print());
//    }
//}
