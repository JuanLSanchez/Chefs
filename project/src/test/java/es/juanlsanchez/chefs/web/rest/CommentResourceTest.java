package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.TestConstants;
import es.juanlsanchez.chefs.domain.Comment;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.repository.CommentRepository;

import es.juanlsanchez.chefs.repository.SocialEntityRepository;
import es.juanlsanchez.chefs.repository.UserRepository;
import es.juanlsanchez.chefs.service.CommentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the CommentResource REST controller.
 *
 * @see CommentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CommentResourceTest {

    private static final String DEFAULT_BODY = "SAMPLE_TEXT";
    private static final String UPDATED_BODY = "UPDATED_TEXT";

    private String login;

    @Inject
    private CommentService commentService;

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SocialEntityRepository socialEntityRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCommentMockMvc;


    private Authentication authentication;

    @Inject
    private ApplicationContext context;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CommentResource commentResource = new CommentResource();
        ReflectionTestUtils.setField(commentResource, "commentService", commentService);
        this.restCommentMockMvc = MockMvcBuilders.standaloneSetup(commentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);

        login = userRepository.findAll().stream()
            .filter(u -> u.getId().compareTo(5L)>=0)
            .filter(u -> u.getMakeRequests().size()==0).findFirst().get().getLogin();

        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login, "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @Transactional
    public void createComment() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        Long socialEntityId;

        socialEntityId = socialEntityRepository.findAll().stream()
            .filter(s -> s.getIsPublic())
            .findFirst().get()
            .getId();

        // Create the Comment

        restCommentMockMvc.perform(post("/api/comments/{socialEntityId}", socialEntityId)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(DEFAULT_BODY)))
            .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getCreationMoment().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testComment.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testComment.getUser().getLogin()).isEqualTo(login);
    }

    @Test
    @Transactional
    public void createCommentWithoutVisibility() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        Long socialEntityId;

        socialEntityId = socialEntityRepository.findAll().stream()
            .filter(s -> !s.getIsPublic())
            .findFirst().get()
            .getId();

        // Create the Comment

        restCommentMockMvc.perform(post("/api/comments/{socialEntityId}", socialEntityId)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(DEFAULT_BODY)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createCommentInPrivate() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        SocialEntity socialEntity;

        socialEntity = socialEntityRepository.findAll().stream()
            .filter(s -> !s.getIsPublic())
            .filter(s -> !s.getBlocked())
            .filter(s -> s.getRecipe() != null)
            .filter(s -> s.getRecipe().getUser().getAcceptRequests().stream()
                .filter(req -> req.getAccepted()).collect(Collectors.toList()).size() > 0)
            .findFirst().get();


        login = socialEntity.getRecipe().getUser().getAcceptRequests()
            .stream().filter(request -> request.getAccepted()).findFirst()
            .get().getFollower().getLogin();
        authentication = this.context.getBean(AuthenticationManager.class).authenticate(
            new UsernamePasswordAuthenticationToken(login, "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create the Comment

        restCommentMockMvc.perform(post("/api/comments/{socialEntityId}", socialEntity.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(DEFAULT_BODY)))
            .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getCreationMoment().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testComment.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testComment.getUser().getLogin()).isEqualTo(login);
    }

    @Test
    @Transactional
    public void createCommentInBlocked() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        SocialEntity socialEntity;

        socialEntity = socialEntityRepository.findAll().stream()
            .filter(s -> s.getBlocked())
            .filter(s -> s.getRecipe() != null)
            .filter(s -> s.getRecipe().getUser().getAcceptRequests().stream()
                .filter(req -> req.getAccepted()).collect(Collectors.toList()).size() > 0)
            .findFirst().get();


        login = socialEntity.getRecipe().getUser().getAcceptRequests()
            .stream().filter(request -> request.getAccepted()).findFirst()
            .get().getFollower().getLogin();
        authentication = this.context.getBean(AuthenticationManager.class).authenticate(
            new UsernamePasswordAuthenticationToken(login, "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create the Comment

        restCommentMockMvc.perform(post("/api/comments/{socialEntityId}", socialEntity.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(DEFAULT_BODY)))
            .andExpect(status().isBadRequest());

        // Validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void createCommentInBlockedAsOwner() throws Exception {
        int databaseSizeBeforeCreate = commentRepository.findAll().size();
        SocialEntity socialEntity;

        socialEntity = socialEntityRepository.findAll().stream()
            .filter(s -> s.getBlocked())
            .filter(s -> s.getRecipe() != null)
            .filter(s -> s.getRecipe().getUser().getAcceptRequests().stream()
                .filter(req -> req.getAccepted()).collect(Collectors.toList()).size() > 0)
            .findFirst().get();


        login = socialEntity.getRecipe().getUser().getLogin();
        authentication = this.context.getBean(AuthenticationManager.class).authenticate(
            new UsernamePasswordAuthenticationToken(login, "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create the Comment

        restCommentMockMvc.perform(post("/api/comments/{socialEntityId}", socialEntity.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(DEFAULT_BODY)))
            .andExpect(status().isCreated());

        // Validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeCreate + 1);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getCreationMoment().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testComment.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testComment.getUser().getLogin()).isEqualTo(login);
    }
/*
    @Test
    @Transactional
    public void checkCreationMomentIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setCreationMoment(null);

        // Create the Comment, which fails.

        restCommentMockMvc.perform(post("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
                .andExpect(status().isBadRequest());

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBodyIsRequired() throws Exception {
        int databaseSizeBeforeTest = commentRepository.findAll().size();
        // set the field null
        comment.setBody(null);

        // Create the Comment, which fails.

        restCommentMockMvc.perform(post("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
                .andExpect(status().isBadRequest());

        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllComments() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the comments
        restCommentMockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(comment.getId().intValue())))
                .andExpect(jsonPath("$.[*].creationMoment").value(hasItem(DEFAULT_CREATION_MOMENT_STR)))
                .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())));
    }

    @Test
    @Transactional
    public void getComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", comment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(comment.getId().intValue()))
            .andExpect(jsonPath("$.creationMoment").value(DEFAULT_CREATION_MOMENT_STR))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingComment() throws Exception {
        // Get the comment
        restCommentMockMvc.perform(get("/api/comments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

		int databaseSizeBeforeUpdate = commentRepository.findAll().size();

        // Update the comment
        comment.setCreationMoment(UPDATED_CREATION_MOMENT);
        comment.setBody(UPDATED_BODY);


        restCommentMockMvc.perform(put("/api/comments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(comment)))
                .andExpect(status().isOk());

        // Validate the Comment in the database
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeUpdate);
        Comment testComment = comments.get(comments.size() - 1);
        assertThat(testComment.getCreationMoment().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_CREATION_MOMENT);
        assertThat(testComment.getBody()).isEqualTo(UPDATED_BODY);
    }

    @Test
    @Transactional
    public void deleteComment() throws Exception {
        // Initialize the database
        commentRepository.saveAndFlush(comment);

		int databaseSizeBeforeDelete = commentRepository.findAll().size();

        // Get the comment
        restCommentMockMvc.perform(delete("/api/comments/{id}", comment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments).hasSize(databaseSizeBeforeDelete - 1);
    }*/
}
