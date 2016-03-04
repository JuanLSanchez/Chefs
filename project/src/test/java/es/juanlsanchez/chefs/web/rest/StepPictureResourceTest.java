package es.juanlsanchez.chefs.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.Step;
import es.juanlsanchez.chefs.domain.StepPicture;
import es.juanlsanchez.chefs.service.RecipeService;
import org.assertj.core.api.StrictAssertions;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
 * Test class for the StepPictureResource REST controller.
 *
 * @see StepPictureResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class StepPictureResourceTest {


    private static final Integer DEFAULT_POSITION = 0;
    private static final String DEFAULT_SECTION_01 = "SAMPLE_TEXT_01";

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

    private static final String DEFAULT_TITLE_01 = "SAMPLE_TEXT_01";
    private static final String DEFAULT_TITLE_02 = "SAMPLE_TEXT_02";
    private static final String DEFAULT_TITLE_03 = "SAMPLE_TEXT_03";

    private static final byte[] DEFAULT_SRC = TestUtil.createByteArray(1, "0");
    private static final String DEFAULT_PROPERTIES = "SAMPLE_TEXT";

    @Inject
    private RecipeService recipeService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecipeMockMvc;

    private Step step01;
    private Recipe recipe;
    private StepPicture stepPicture01,stepPicture02,stepPicture03;

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
        stepPicture01 = new StepPicture();
        stepPicture01.setTitle(DEFAULT_TITLE_01);
        stepPicture01.setSrc(DEFAULT_SRC);
        stepPicture01.setProperties(DEFAULT_PROPERTIES);

        stepPicture02 = new StepPicture();
        stepPicture02.setTitle(DEFAULT_TITLE_02);
        stepPicture02.setSrc(DEFAULT_SRC);
        stepPicture02.setProperties(DEFAULT_PROPERTIES);

        stepPicture03 = new StepPicture();
        stepPicture03.setTitle(DEFAULT_TITLE_03);
        stepPicture03.setSrc(DEFAULT_SRC);
        stepPicture03.setProperties(DEFAULT_PROPERTIES);

        step01 = new Step();
        step01.setPosition(DEFAULT_POSITION);
        step01.setSection(DEFAULT_SECTION_01);
        step01.getStepPicture().add(stepPicture01);
        step01.getStepPicture().add(stepPicture02);

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

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
        this.friendAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(FRIEND_LOGIN_USER, FRIEND_USER_PASSWORD));
    }

    @Test
    @Transactional
    public void createRecipeWithStepsPicture() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();
        int recipeId;

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[0].stepPicture[*].title",
                Matchers.containsInAnyOrder(stepPicture01.getTitle(), stepPicture02.getTitle())))
            .andExpect(jsonPath("$steps[0].stepPicture", Matchers.hasSize(2)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        try {
            recipeId = (new ObjectMapper())
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), Recipe.class)
                .getId().intValue();

            restRecipeMockMvc.perform(get("/api/recipes/"+recipeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$steps[0].stepPicture[*].title",
                    Matchers.containsInAnyOrder(stepPicture01.getTitle(), stepPicture02.getTitle())))
                .andExpect(jsonPath("$steps[0].stepPicture", Matchers.hasSize(2)))
                .andExpect(jsonPath("$name").value(recipe.getName()));

        }catch (Exception e){
            assertThat(false);
        }

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        StrictAssertions.assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        StrictAssertions.assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        StrictAssertions.assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        StrictAssertions.assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
        StrictAssertions.assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isEqualTo(testRecipe.getUpdateDate());
    }

    @Test
    @Transactional
    public void createRecipeWithStepsAndAfterAddTwoStepAndDeleteOne() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();

        // Create the Recipe
        recipeService.save(recipe);

        // Update the Recipe
        step01.setStepPicture(Sets.newHashSet(stepPicture02));
        step01.getStepPicture().add(stepPicture03);

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[0].stepPicture[*].title",
                Matchers.containsInAnyOrder(stepPicture03.getTitle(), stepPicture02.getTitle())))
            .andExpect(jsonPath("$steps[0].stepPicture", Matchers.hasSize(2)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        restRecipeMockMvc.perform(get("/api/recipes/"+recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[0].stepPicture[*].title",
                Matchers.containsInAnyOrder(stepPicture03.getTitle(), stepPicture02.getTitle())))
            .andExpect(jsonPath("$steps[0].stepPicture", Matchers.hasSize(2)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        StrictAssertions.assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        StrictAssertions.assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        StrictAssertions.assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        StrictAssertions.assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
        StrictAssertions.assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
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

        // Clone the Recipe
        step01.setStepPicture(Sets.newHashSet(stepPicture01));
        step01.getStepPicture().add(stepPicture03);

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[0].stepPicture[*].title",
                Matchers.containsInAnyOrder(stepPicture01.getTitle(), stepPicture03.getTitle())))
            .andExpect(jsonPath("$steps[0].stepPicture", Matchers.hasSize(2)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        try {
            cloneRecipeId = (new ObjectMapper())
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), Recipe.class)
                .getId().intValue();

            restRecipeMockMvc.perform(get("/api/recipes/"+cloneRecipeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$steps[0].stepPicture[*].title",
                    Matchers.containsInAnyOrder(stepPicture01.getTitle(), stepPicture03.getTitle())))
                .andExpect(jsonPath("$steps[0].stepPicture", Matchers.hasSize(2)))
                .andExpect(jsonPath("$name").value(recipe.getName()));

        }catch (Exception e){
            assertThat(false);
        }
    }
}
