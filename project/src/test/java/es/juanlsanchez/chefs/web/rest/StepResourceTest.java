package es.juanlsanchez.chefs.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.Step;
import es.juanlsanchez.chefs.repository.StepRepository;

import es.juanlsanchez.chefs.service.RecipeService;
import es.juanlsanchez.chefs.service.StepService;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the StepResource REST controller.
 *
 * @see StepResource
 */
/** TODO: Make tests */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class StepResourceTest {


    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;
    private static final String DEFAULT_SECTION_01 = "SAMPLE_TEXT_01";
    private static final String DEFAULT_SECTION_02 = "SAMPLE_TEXT_02";
    private static final String DEFAULT_SECTION_03 = "SAMPLE_TEXT_03";
    private static final String DEFAULT_SECTION_04 = "SAMPLE_TEXT_04";
    private static final String UPDATED_SECTION = "UPDATED_TEXT";

    private static final String DEFAULT_LOGIN_USER = "user002";
    private static final String DEFAULT_USER_PASSWORD = "user";

    private static final String FRIEND_LOGIN_USER = "user001";
    private static final String FRIEND_USER_PASSWORD = "user";

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";

    private static final String DEFAULT_INFORMATION_URL = "SAMPLE_TEXT";
    private static final String DEFAULT_ADVICE = "SAMPLE_TEXT";
    private static final String DEFAULT_SUGESTED_TIME = "SAMPLE_TEXT";

    private static final Boolean DEFAULT_INGREDIENTS_IN_STEPS = false;

    private static final Boolean DEFAULT_SOCIAL_ENTITY_BLOCKED = false;
    private static final Boolean DEFAULT_SOCIAL_ENTITY_IS_PUBLIC = true;
    private static final Boolean DEFAULT_SOCIAL_ENTITY_PUBLIC_INSCRIPTION = true;

    @Inject
    private RecipeService recipeService;

    @Inject
    private StepService stepService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecipeMockMvc;

    private Step step01,step02,step03,step04;
    private Recipe recipe;

    private Authentication authentication, friendAuthentication;

    @Inject
    private ApplicationContext context;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecipeResource recipeResource = new RecipeResource();
        ReflectionTestUtils.setField(recipeResource, "recipeService", recipeService);
        this.restRecipeMockMvc = MockMvcBuilders.standaloneSetup(recipeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        step01 = new Step();
        step01.setPosition(DEFAULT_POSITION);
        step01.setSection(DEFAULT_SECTION_01);

        step02 = new Step();
        step02.setPosition(DEFAULT_POSITION + 1);
        step02.setSection(DEFAULT_SECTION_02);

        step03 = new Step();
        step03.setPosition(DEFAULT_POSITION + 2);
        step03.setSection(DEFAULT_SECTION_03);

        step04 = new Step();
        step04.setPosition(DEFAULT_POSITION);
        step04.setSection(DEFAULT_SECTION_04);

        SocialEntity socialEntity = new SocialEntity();
        socialEntity.setBlocked(DEFAULT_SOCIAL_ENTITY_BLOCKED);
        socialEntity.setIsPublic(DEFAULT_SOCIAL_ENTITY_IS_PUBLIC);
        socialEntity.setPublicInscription(DEFAULT_SOCIAL_ENTITY_PUBLIC_INSCRIPTION);

        recipe = new Recipe();
        recipe.setName(DEFAULT_NAME);
        recipe.setDescription(DEFAULT_DESCRIPTION);
        recipe.setInformationUrl(DEFAULT_INFORMATION_URL);
        recipe.setAdvice(DEFAULT_ADVICE);
        recipe.setSugestedTime(DEFAULT_SUGESTED_TIME);
        recipe.setIngredientsInSteps(DEFAULT_INGREDIENTS_IN_STEPS);
        recipe.setSocialEntity(socialEntity);
        recipe.setCreationDate(new DateTime());
        recipe.getSteps().add(step01);
        recipe.getSteps().add(step02);

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
        this.friendAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(FRIEND_LOGIN_USER, FRIEND_USER_PASSWORD));
    }

    @Test
    @Transactional
    public void createRecipeWithSteps() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();
        Integer stepsSizeBeforeCreate = stepService.findAll().size();

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[0].section").value(step01.getSection()))
            .andExpect(jsonPath("$steps[1].section").value(step02.getSection()))
            .andExpect(jsonPath("$steps", Matchers.hasSize(2)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isEqualTo(testRecipe.getUpdateDate());

        // Get all the steps
        assertThat(stepsSizeBeforeCreate).isEqualTo(stepService.findAll().size() - 2);
    }

    @Test
    @Transactional
    public void createRecipeWithStepsAndAfterAddTwoStepAndDeleteOne() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();
        Integer stepsSizeBeforeCreate = stepService.findAll().size();

        // Create the Recipe
        recipeService.save(recipe);

        // Update the Recipe
        recipe.getSteps().remove(step02);
        recipe.getSteps().add(step03);
        recipe.getSteps().add(step04);
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[*].section",
                Matchers.containsInAnyOrder(step01.getSection(), step03.getSection(), step04.getSection())))
            .andExpect(jsonPath("$steps", Matchers.hasSize(3)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        restRecipeMockMvc.perform(get("/api/recipes/"+recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[*].section",
                Matchers.containsInAnyOrder(step01.getSection(), step03.getSection(), step04.getSection())))
            .andExpect(jsonPath("$steps", Matchers.hasSize(3)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isLessThan(testRecipe.getUpdateDate());
    }

    @Test
    @Transactional
    public void cloneRecipeWithStepsAndAddStep() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int cloneRecipeId;

        // Update the Recipe
        recipeService.save(recipe);

        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();
        Integer stepsSizeBeforeCreate = stepService.findAll().size();

        // Clone the Recipe
        recipe.getSteps().removeIf(step -> step.getSection().equals(step02.getSection()));
        recipe.getSteps().add(step03);
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[*].section",
                Matchers.containsInAnyOrder(step01.getSection(), step03.getSection())))
            .andExpect(jsonPath("$steps", Matchers.hasSize(2)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        try {
            cloneRecipeId = (new ObjectMapper())
                                .readValue(resultActions.andReturn().getResponse().getContentAsString(), Recipe.class)
                                    .getId().intValue();

            restRecipeMockMvc.perform(get("/api/recipes/"+cloneRecipeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$steps[*].section",
                    Matchers.containsInAnyOrder(step01.getSection(), step03.getSection())))
                .andExpect(jsonPath("$steps", Matchers.hasSize(2)))
                .andExpect(jsonPath("$name").value(recipe.getName()));

        }catch (Exception e){
            assertThat(false);
        }

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(FRIEND_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isLessThan(testRecipe.getUpdateDate());
    }

}
