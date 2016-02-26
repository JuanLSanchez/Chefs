package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.TestConstants;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.service.RecipeService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
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
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the RecipeResource REST controller.
 *
 * @see RecipeResource
 */
/** TODO: Make tests */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Ignore
public class RecipeResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final String DEFAULT_INFORMATION_URL = "SAMPLE_TEXT";
    private static final String UPDATED_INFORMATION_URL = "UPDATED_TEXT";
    private static final String DEFAULT_ADVICE = "SAMPLE_TEXT";
    private static final String UPDATED_ADVICE = "UPDATED_TEXT";
    private static final String DEFAULT_SUGESTED_TIME = "SAMPLE_TEXT";
    private static final String UPDATED_SUGESTED_TIME = "UPDATED_TEXT";

    private static final Boolean DEFAULT_INGREDIENTS_IN_STEPS = false;
    private static final Boolean UPDATED_INGREDIENTS_IN_STEPS = true;

    private static final String DEFAULT_LOGIN_USER = "user001";
    private static final String DEFAULT_USER_PASSWORD = "user";

    private static final Boolean DEFAULT_SOCIAL_ENTITY_BLOCKED = false;
    private static final Boolean DEFAULT_SOCIAL_ENTITY_IS_PUBLIC = true;
    private static final Boolean DEFAULT_SOCIAL_ENTITY_PUBLIC_INSCRIPTION = true;

    @Inject
    private RecipeService recipeService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    private Authentication authentication;
    @Autowired
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
        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
    }

    @Test
    @Transactional
    public void createRecipe() throws Exception {
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
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeService.findAll().size();
        // set the field null
        recipe.setName(null);

        // Create the Recipe, which fails.

        restRecipeMockMvc.perform(post("/api/recipes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isBadRequest());

        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeService.findAll().size();
        // set the field null
        recipe.setDescription(null);

        // Create the Recipe, which fails.

        restRecipeMockMvc.perform(post("/api/recipes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isBadRequest());

        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeService.findAll().size();
        // set the field null
        recipe.setCreationDate(null);

        // Create the Recipe, which fails.

        restRecipeMockMvc.perform(post("/api/recipes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isBadRequest());

        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecipes() throws Exception {
        // Initialize the database
        recipeService.saveAndFlush(recipe);

        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the recipes
        restRecipeMockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                .andExpect(jsonPath("$.[*].informationUrl").value(hasItem(DEFAULT_INFORMATION_URL)))
                .andExpect(jsonPath("$.[*].advice").value(hasItem(DEFAULT_ADVICE)))
                .andExpect(jsonPath("$.[*].sugestedTime").value(hasItem(DEFAULT_SUGESTED_TIME)))
                .andExpect(jsonPath("$.[*].ingredientsInSteps").value(hasItem(DEFAULT_INGREDIENTS_IN_STEPS.booleanValue())));
    }

    @Test
    @Transactional
    public void getRecipe() throws Exception {
        // Initialize the database
        recipeService.saveAndFlush(recipe);

        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recipe.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.informationUrl").value(DEFAULT_INFORMATION_URL))
            .andExpect(jsonPath("$.advice").value(DEFAULT_ADVICE))
            .andExpect(jsonPath("$.sugestedTime").value(DEFAULT_SUGESTED_TIME))
            .andExpect(jsonPath("$.ingredientsInSteps").value(DEFAULT_INGREDIENTS_IN_STEPS));
    }

    @Test
    @Transactional
    public void getNonExistingRecipe() throws Exception {
        //Login
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("user001", "user"));
        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecipe() throws Exception {
        // Initialize the database
        recipeService.saveAndFlush(recipe);

		int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);


        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThan(
            testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC));
        assertThat(testRecipe.getInformationUrl()).isEqualTo(UPDATED_INFORMATION_URL);
        assertThat(testRecipe.getAdvice()).isEqualTo(UPDATED_ADVICE);
        assertThat(testRecipe.getSugestedTime()).isEqualTo(UPDATED_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(UPDATED_INGREDIENTS_IN_STEPS);
    }
}
