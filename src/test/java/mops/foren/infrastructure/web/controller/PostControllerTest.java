package mops.foren.infrastructure.web.controller;

import mops.foren.applicationservices.PostService;
import mops.foren.applicationservices.ThreadService;
import mops.foren.applicationservices.UserService;
import mops.foren.domain.model.*;
import mops.foren.infrastructure.web.Account;
import mops.foren.infrastructure.web.KeycloakService;
import mops.foren.infrastructure.web.KeycloakTokenMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    MockMvc mvcMock;

    @Autowired
    WebApplicationContext context;

    @MockBean
    KeycloakService keycloakServiceMock;

    @MockBean
    UserService userServiceMock;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    ThreadService threadServiceMock;

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    PostService postServiceMock;

    @MockBean
    User userMock;

    /**
     * Building up a security environment for the Test.
     */
    @BeforeEach
    public void setup() {
        this.mvcMock = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        KeycloakTokenMock.setupTokenMock(Account.builder()
                .name("studentin")
                .roles(Set.of("studentin"))
                .build());
    }

    @Test
    void testAddNewPostWithoutPermission() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any())).thenReturn(false);

        this.mvcMock.perform(post("/foren/post/new-post?threadId=1&page=0")
                .param("postContent", "    ")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error-no-permission"));
    }

    @Test
    void testAddNewPostWithPermissionView() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any())).thenReturn(true);
        when((threadServiceMock.getThreadById(any()).getId())).thenReturn(new ThreadId(1L));

        this.mvcMock.perform(post("/foren/post/new-post?threadId=1&page=0")
                .param("postContent", "    ")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/foren/thread?threadId=1&page=1"));
    }

    @Test
    void testAddNewPostWithPermissionInvokeAddPost() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any())).thenReturn(true);
        when((threadServiceMock.getThreadById(any()).getId())).thenReturn(new ThreadId(1L));

        this.mvcMock.perform(post("/foren/post/new-post?threadId=1&page=0")
                .param("postContent", "    ")
                .with(csrf()));

        verify(threadServiceMock).addPostInThread(any(), any());
    }

    @Test
    void testAddNewPostBindingResultFails() throws Exception {

        this.mvcMock.perform(post("/foren/post/new-post?threadId=1&page=0")
                .param("postContent", "")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/foren/thread?threadId=1&page=1"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void testApprovePostWithoutPermission() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any())).thenReturn(false);

        this.mvcMock.perform(post("/foren/post/approve-post?postId=1&page=0")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error-no-permission"));
    }

    @Test
    void testApprovePostWithPermissionView() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any())).thenReturn(true);
        when((postServiceMock.getPost(any()).getThreadId())).thenReturn(new ThreadId(1L));

        this.mvcMock.perform(post("/foren/post/approve-post?postId=1&page=0")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/foren/thread?threadId=1&page=1"));
    }

    @Test
    void testApprovePostWithPermissionInvokeApprovePost() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any())).thenReturn(true);
        when((postServiceMock.getPost(any()).getThreadId())).thenReturn(new ThreadId(1L));

        this.mvcMock.perform(post("/foren/post/approve-post?postId=1&page=0")
                .with(csrf()));

        verify(postServiceMock).setPostVisible(any());
    }

    @Test
    void testDeletePostWithoutPermission() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any())).thenReturn(false);

        this.mvcMock.perform(post("/foren/post/delete-post?postId=1&page=0")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error-no-permission"));
    }

    @Test
    void testDeletePostWithPermissionView() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any(), any())).thenReturn(true);
        when(postServiceMock.getPost(any())).thenReturn(Post.builder()
                .id(new PostId(1L))
                .threadId(new ThreadId(1L))
                .forumId(new ForumId(1L))
                .build());

        this.mvcMock.perform(post("/foren/post/delete-post?postId=1&page=0")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/foren/thread?threadId=1&page=1"));
    }

    @Test
    void testDeletePostWithPermissionInvokeDeletePost() throws Exception {

        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any(), any())).thenReturn(true);
        when(postServiceMock.getPost(any())).thenReturn(Post.builder()
                .id(new PostId(1L))
                .threadId(new ThreadId(1L))
                .forumId(new ForumId(1L))
                .build());

        this.mvcMock.perform(post("/foren/post/delete-post?postId=1&page=0")
                .with(csrf()));

        verify(postServiceMock).deletePost(any());
    }

    @Test
    void testAccountIsInModel() throws Exception {
        Account fakeAccount = Account.builder()
                .name("orga")
                .roles(Set.of("orga"))
                .build();
        KeycloakTokenMock.setupTokenMock(fakeAccount);

        when(keycloakServiceMock.createAccountFromPrincipal(any(KeycloakAuthenticationToken.class)))
                .thenReturn(fakeAccount);
        when(userServiceMock.getUserFromDB(any())).thenReturn(userMock);
        when(userMock.checkPermission(any(), any(), any())).thenReturn(false);

        this.mvcMock.perform(post("/foren/post/delete-post?postId=1&page=0")
                .with(csrf()))
                .andExpect(model().attributeExists("account"));
    }

}
