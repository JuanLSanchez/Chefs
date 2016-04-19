package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Assessment;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.AssessmentRepository;
import es.juanlsanchez.chefs.repository.RecipeRepository;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;
import es.juanlsanchez.chefs.repository.UserRepository;
import es.juanlsanchez.chefs.service.AssessmentService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the AssessmentResource REST controller.
 *
 * @see AssessmentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AssessmentResourceTest {


    private static final Integer DEFAULT_RATING = 5;

    private String login;

    @Inject
    private AssessmentService assessmentService;

    @Inject
    private AssessmentRepository assessmentRepository;

    @Inject
    private RecipeRepository recipeRepository;

    @Inject
    private SocialEntityRepository socialEntityRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private UserRepository userRepository;

    private MockMvc restAssessmentMockMvc;

    @Inject
    private ApplicationContext context;

    private Authentication authentication;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AssessmentResource assessmentResource = new AssessmentResource();
        ReflectionTestUtils.setField(assessmentResource, "assessmentService", assessmentService);
        this.restAssessmentMockMvc = MockMvcBuilders.standaloneSetup(assessmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);

        login = userRepository.findAll().stream()
            .filter(u -> u.getId().compareTo(5L)>=0)
            .filter(u -> u.getAssessments().size()==0)
            .filter(u -> u.getAcceptRequests().size()==0)
            .filter(u -> u.getRecipes().size() == 0)
            .findFirst().get().getLogin();

        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login, "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Test
    @Transactional
    public void updateAssessment() throws Exception {
        int databaseSizeBeforeCreate = assessmentRepository.findAll().size();
        Recipe recipe;
        Integer rating = DEFAULT_RATING;
        Double value;
        ResultActions response;
        User user;

        user = userRepository.findAll().stream()
            .filter(u -> u.getId().compareTo(5L)>=0)
            .filter(u -> u.getAssessments().stream()
                .filter(assessment -> assessment.getSocialEntity().getRecipe() != null)
                .collect(Collectors.toList()).size()!=0)
            .findFirst().get();

        login = user.getLogin();

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);

        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login, "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        recipe = user.getAssessments().stream()
            .filter(a -> a.getSocialEntity().getRecipe()!=null)
            .findFirst().get()
            .getSocialEntity().getRecipe();

        value = (recipe.getSocialEntity().getSumRating().doubleValue()
            + rating-user.getAssessments().stream()
            .filter(assessment -> assessment.getSocialEntity().equals(recipe.getSocialEntity()))
            .findFirst().get().getRating())/
            (recipe.getSocialEntity().getAssessments().size());

        // Create the Assessment
        response = restAssessmentMockMvc.perform(put("/api/assessments/{socialEntityId}", recipe.getSocialEntity().getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rating)));

        response
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.comparesEqualTo(value)));

        // Validate the Assessment in the database
        List<Assessment> assessments = assessmentRepository.findAll();
        assertThat(assessments).hasSize(databaseSizeBeforeCreate );
    }
    @Test
    @Transactional
    public void createAssessment() throws Exception {
        int databaseSizeBeforeCreate = assessmentRepository.findAll().size();
        Recipe recipe;
        Integer rating = DEFAULT_RATING;
        Double value;
        ResultActions response;

        recipe = recipeRepository.findAll().stream()
            .filter(r -> r.getSocialEntity().getIsPublic())
            .findFirst().get();

        value = (recipe.getSocialEntity().getSumRating().doubleValue()+rating)/
            (recipe.getSocialEntity().getAssessments().size()+1);

        // Create the Assessment
        response = restAssessmentMockMvc.perform(put("/api/assessments/{socialEntityId}", recipe.getSocialEntity().getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rating)));

        response
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.comparesEqualTo(value)));

        // Validate the Assessment in the database
        List<Assessment> assessments = assessmentRepository.findAll();
        assertThat(assessments).hasSize(databaseSizeBeforeCreate + 1);
        Assessment testAssessment = assessments.get(assessments.size() - 1);
        assertThat(testAssessment.getRating()).isEqualTo(rating);
    }
    @Test
    @Transactional
    public void createAssessmentWithoutPermision() throws Exception {
        int databaseSizeBeforeCreate = assessmentRepository.findAll().size();
        Recipe recipe;
        Integer rating = DEFAULT_RATING;
        ResultActions response;

        recipe = recipeRepository.findAll().stream()
            .filter(r -> !r.getSocialEntity().getIsPublic())
            .findFirst().get();

        // Create the Assessment
        response = restAssessmentMockMvc.perform(put("/api/assessments/{socialEntityId}", recipe.getSocialEntity().getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(rating)));

        response
            .andExpect(status().isBadRequest());

        // Validate the Assessment in the database
        List<Assessment> assessments = assessmentRepository.findAll();
        assertThat(assessments).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAssessmentOfRecipe() throws Exception {
        // Initialize the database
        Long socialEntityId;
        Recipe recipe;
        Double assessmentsRating;

        recipe = recipeRepository.findAll().stream()
            .filter(r -> r.getSocialEntity().getIsPublic())
            .filter(r -> r.getSocialEntity().getAssessments().size()>0)
            .findFirst().get();

        socialEntityId = recipe.getSocialEntity().getId();

        assessmentsRating = (recipe.getSocialEntity().getSumRating().doubleValue()) /
            (recipe.getSocialEntity().getAssessments().size());

        // Get the assessment
        restAssessmentMockMvc.perform(get("/api/assessments/{socialEntityId}", socialEntityId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").value(assessmentsRating));
    }

    @Test
    @Transactional
    public void getAssessmentOfRecipeWithoutPermision() throws Exception {
        // Initialize the database
        Long socialEntityId;
        Recipe recipe;

        recipe = recipeRepository.findAll().stream()
            .filter(r -> !r.getSocialEntity().getIsPublic())
            .filter(r -> r.getSocialEntity().getAssessments().size()>0)
            .findFirst().get();

        socialEntityId = recipe.getSocialEntity().getId();


        // Get the assessment
        restAssessmentMockMvc.perform(get("/api/assessments/{socialEntityId}", socialEntityId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void getAssessmentOfUser() throws Exception {
        // Initialize the database
        Long socialEntityId;
        Integer assessmentsRating;
        User user;
        SocialEntity socialEntity;

        user = userRepository.findAll().stream()
            .filter(u -> u.getId().compareTo(5L)>=0)
            .filter(u -> u.getAssessments().size()>0)
            .findFirst().get();

        login = user.getLogin();

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);

        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login, "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        socialEntity = user.getAssessments().stream()
            .findFirst().get()
            .getSocialEntity();
        socialEntityId = socialEntity.getId();

        assessmentsRating = socialEntity.getAssessments().stream()
            .filter(s -> s.getUser().getLogin().equals(login))
            .findFirst().get().getRating();

        // Get the assessment
        restAssessmentMockMvc.perform(get("/api/assessments/user/{socialEntityId}", socialEntityId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").value(assessmentsRating));
    }

    @Test
    @Transactional
    public void getAssessmentOfUserInSocialEntityWithoutAssessmentOfUser() throws Exception {
        // Initialize the database
        Long socialEntityId;
        SocialEntity socialEntity;
        Recipe recipe;

        recipe = recipeRepository.findAll().stream()
            .filter(r -> r.getSocialEntity().getIsPublic())
            .filter(r -> r.getSocialEntity().getAssessments().size()>0)
            .findFirst().get();

        socialEntity = recipe.getSocialEntity();
        socialEntityId = socialEntity.getId();

        // Get the assessment
        restAssessmentMockMvc.perform(get("/api/assessments/user/{socialEntityId}", socialEntityId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(-1));
    }

    @Test
    @Transactional
    public void deleteAssessment() throws Exception {
        User user;
        SocialEntity socialEntity;
        Assessment assessment;

		int databaseSizeBeforeDelete = assessmentRepository.findAll().size();

        user = userRepository.findAll().stream()
            .filter(u -> u.getId().compareTo(5L) >= 0)
            .filter(u -> u.getAssessments().size() > 0)
            .findFirst().get();

        login = user.getLogin();

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);

        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login, "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        socialEntity = user.getAssessments().stream()
            .findFirst().get()
            .getSocialEntity();
        Long socialEntityId = socialEntity.getId();
        assessment = socialEntity.getAssessments().stream()
            .filter(s -> s.getUser().getLogin().equals(login))
            .findFirst().get();

        Integer socialEntitySumRatingAfter = socialEntity.getSumRating() - assessment.getRating();

        // Get the assessment
        restAssessmentMockMvc.perform(delete("/api/assessments/user/{socialEntityId}", socialEntityId)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Assessment> assessments = assessmentRepository.findAll();
        socialEntity = socialEntityRepository.findOne(socialEntityId);
        assertThat(assessments).hasSize(databaseSizeBeforeDelete - 1);
        assertThat(socialEntity.getSumRating()).isEqualTo(socialEntitySumRatingAfter);
    }
}
