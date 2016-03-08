package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.service.RecipeService;
import es.juanlsanchez.chefs.service.SocialEntityService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Test class for the SocialEntityResource REST controller.
 *
 * @see SocialEntityResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SocialEntityResourceTest {

    private static final String DEFAULT_SEARCH = "recipe";

    private static final String DEFAULT_NAME = DEFAULT_SEARCH+"SAMPLE_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";

    private static final String DEFAULT_INFORMATION_URL = "SAMPLE_TEXT";
    private static final String DEFAULT_ADVICE = "SAMPLE_TEXT";
    private static final String DEFAULT_SUGESTED_TIME = "SAMPLE_TEXT";

    private static final Boolean DEFAULT_INGREDIENTS_IN_STEPS = false;

    private static final String DEFAULT_LOGIN_USER = "user002";
    private static final String DEFAULT_USER_PASSWORD = "user";

    private static final Boolean DEFAULT_SOCIAL_ENTITY_BLOCKED = false;
    private static final Boolean DEFAULT_SOCIAL_ENTITY_IS_PUBLIC = true;
    private static final Boolean DEFAULT_SOCIAL_ENTITY_PUBLIC_INSCRIPTION = true;
    private static final Integer MODIFIED_SUM_RATING = 22;

    @Inject
    private RecipeService recipeService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    private Authentication authentication;

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

        SocialEntity secondSocialEntity = new SocialEntity();
        secondSocialEntity.setBlocked(DEFAULT_SOCIAL_ENTITY_BLOCKED);
        secondSocialEntity.setIsPublic(DEFAULT_SOCIAL_ENTITY_IS_PUBLIC);
        secondSocialEntity.setPublicInscription(DEFAULT_SOCIAL_ENTITY_PUBLIC_INSCRIPTION);

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
    }

    @Test
    @Transactional
    public void createRecipeWithoutSumRating() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions.andExpect(status().isCreated());

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
        assertThat(testRecipe.getSocialEntity().getSumRating()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void createRecipeWithModifiedtSumRating() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();
        recipe.getSocialEntity().setSumRating(MODIFIED_SUM_RATING);

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$socialEntity.sumRating").value(0));

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
        assertThat(testRecipe.getSocialEntity().getSumRating()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void createRecipeWithoutSocialEntity() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        recipe.setSocialEntity(null);

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void createRecipeWithoutSocialEntitySecurity() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();

        recipe.getSocialEntity().setPublicInscription(null);
        recipe.getSocialEntity().setBlocked(null);
        recipe.getSocialEntity().setIsPublic(null);

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions.andExpect(status().isCreated());

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
        // Validate the security
        assertThat(testRecipe.getSocialEntity().getIsPublic()).isEqualTo(SocialEntityService.DEFAULT_IS_PUBLIC);
        assertThat(testRecipe.getSocialEntity().getBlocked()).isEqualTo(SocialEntityService.DEFAULT_BLOCKED);
        assertThat(testRecipe.getSocialEntity().getPublicInscription())
            .isEqualTo(SocialEntityService.DEFAULT_PUBLIC_INSCRIPTION);
    }

    @Test
    @Transactional
    public void createRecipeWithSocialEntitySecurityToFalse() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        final boolean SECURITY_VALUE = false;
        int databaseSizeBeforeCreate = recipeService.findAll().size();

        recipe.getSocialEntity().setPublicInscription(SECURITY_VALUE);
        recipe.getSocialEntity().setBlocked(SECURITY_VALUE);
        recipe.getSocialEntity().setIsPublic(SECURITY_VALUE);

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions.andExpect(status().isCreated());

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
        // Validate the security
        assertThat(testRecipe.getSocialEntity().getIsPublic()).isEqualTo(SECURITY_VALUE);
        assertThat(testRecipe.getSocialEntity().getBlocked()).isEqualTo(SECURITY_VALUE);
        assertThat(testRecipe.getSocialEntity().getPublicInscription()).isEqualTo(SECURITY_VALUE);
    }

    @Test
    @Transactional
    public void createRecipeWithSocialEntitySecurityToTrue() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        final boolean SECURITY_VALUE = true;
        int databaseSizeBeforeCreate = recipeService.findAll().size();

        recipe.getSocialEntity().setPublicInscription(SECURITY_VALUE);
        recipe.getSocialEntity().setBlocked(SECURITY_VALUE);
        recipe.getSocialEntity().setIsPublic(SECURITY_VALUE);

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions.andExpect(status().isCreated());

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
        // Validate the security
        assertThat(testRecipe.getSocialEntity().getIsPublic()).isEqualTo(SECURITY_VALUE);
        assertThat(testRecipe.getSocialEntity().getBlocked()).isEqualTo(SECURITY_VALUE);
        assertThat(testRecipe.getSocialEntity().getPublicInscription()).isEqualTo(SECURITY_VALUE);
    }

}
