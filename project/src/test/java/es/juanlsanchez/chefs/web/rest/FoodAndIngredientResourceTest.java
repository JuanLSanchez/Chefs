package es.juanlsanchez.chefs.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.*;
import es.juanlsanchez.chefs.domain.enumeration.Measurement;
import es.juanlsanchez.chefs.service.FoodService;
import es.juanlsanchez.chefs.service.IngredientService;
import es.juanlsanchez.chefs.service.RecipeService;
import org.apache.commons.lang3.StringUtils;
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
import java.net.URLEncoder;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the FoodResource REST controller.
 *
 * @see FoodResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FoodAndIngredientResourceTest {

    private static final Integer DEFAULT_POSITION = 0;
    private static final String DEFAULT_SECTION_01 = "SAMPLE_TEXT_01";

    private static final String DEFAULT_LOGIN_USER = "user002";
    private static final String DEFAULT_USER_PASSWORD = "user";

    private static final String FRIEND_LOGIN_USER = "user001";
    private static final String FRIEND_USER_PASSWORD = "user";

    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";

    private static final String DEFAULT_INFORMATION_URL = "SAMPLE_TEXT";
    private static final String DEFAULT_ADVICE = "SAMPLE_TEXT";
    private static final String DEFAULT_SUGESTED_TIME = "SAMPLE_TEXT";

    private static final Boolean DEFAULT_INGREDIENTS_IN_STEPS = false;

    private static final Boolean DEFAULT_SOCIAL_ENTITY_BLOCKED = false;
    private static final Boolean DEFAULT_SOCIAL_ENTITY_IS_PUBLIC = true;
    private static final Boolean DEFAULT_SOCIAL_ENTITY_PUBLIC_INSCRIPTION = true;

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";

    private static final Double DEFAULT_KCAL = 0D;
    private static final Double UPDATED_KCAL = 1D;
    private static final Double DEFAULT_AMOUNT_01 = 111D;
    private static final Double DEFAULT_AMOUNT_02 = 222D;
    private static final Double DEFAULT_AMOUNT_03 = 333D;
    private static final Measurement DEFAULT_MEASUREMENT = Measurement.g;
    private static final String DEFAULT_RECIPE_NAME = "RECIPE_NAME_TO_TESTS_01";
    private static final String DEFAULT_INGREDIENT_NAME_01 = "INGREDIENT_01";
    private static final String DEFAULT_NORMALIZED_NAME_01 = "INGREDIENT_01";
    private static final String DEFAULT_INGREDIENT_NAME_02 = "DEFAULT ÍNGREDIENT name 02";
    private static final String DEFAULT_NORMALIZED_NAME_02 = "DEFAULTINGREDIENTNAME02";

    @Inject
    private RecipeService recipeService;

    @Inject
    private FoodService foodService;

    @Inject
    private IngredientService ingredientService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecipeMockMvc, restFoodMockMvc;

    private Step step01;
    private Recipe recipe;
    private Food food01, food02, food03;
    private Ingredient ingredient01, ingredient02, ingredient03;

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

        FoodResource foodResource = new FoodResource();
        ReflectionTestUtils.setField(foodResource, "foodService", foodService);
        this.restFoodMockMvc = MockMvcBuilders.standaloneSetup(foodResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {

        food01 = new Food();
        food01.setName(DEFAULT_INGREDIENT_NAME_01);
        food01.setKcal(DEFAULT_KCAL);
        food01.setNormalizaedName(DEFAULT_NORMALIZED_NAME_01);

        food02 = new Food();
        food02.setName(DEFAULT_INGREDIENT_NAME_02);
        food02.setKcal(DEFAULT_KCAL);
        food02.setNormalizaedName(DEFAULT_INGREDIENT_NAME_02);

        food03 = foodService.findOne(1L);

        ingredient01 = new Ingredient();
        ingredient01.setAmount(DEFAULT_AMOUNT_01);
        ingredient01.setFood(food01);
        food01.getIngredients().add(ingredient01);
        ingredient01.setMeasurement(DEFAULT_MEASUREMENT);

        ingredient02 = new Ingredient();
        ingredient02.setAmount(DEFAULT_AMOUNT_02);
        ingredient02.setMeasurement(DEFAULT_MEASUREMENT);
        ingredient02.setFood(food02);
        food02.getIngredients().add(ingredient02);

        ingredient03 = new Ingredient();
        ingredient03.setAmount(DEFAULT_AMOUNT_03);
        ingredient03.setMeasurement(DEFAULT_MEASUREMENT);
        ingredient03.setFood(food03);
        food03.getIngredients().add(ingredient03);

        step01 = new Step();
        step01.setPosition(DEFAULT_POSITION);
        step01.setSection(DEFAULT_SECTION_01);
        step01.getIngredients().add(ingredient01);
        ingredient01.setStep(step01);
        step01.getIngredients().add(ingredient02);
        ingredient02.setStep(step01);

        SocialEntity socialEntity = new SocialEntity();
        socialEntity.setBlocked(DEFAULT_SOCIAL_ENTITY_BLOCKED);
        socialEntity.setIsPublic(DEFAULT_SOCIAL_ENTITY_IS_PUBLIC);
        socialEntity.setPublicInscription(DEFAULT_SOCIAL_ENTITY_PUBLIC_INSCRIPTION);

        recipe = new Recipe();
        recipe.setName(DEFAULT_RECIPE_NAME);
        recipe.setDescription(DEFAULT_DESCRIPTION);
        recipe.setInformationUrl(DEFAULT_INFORMATION_URL);
        recipe.setAdvice(DEFAULT_ADVICE);
        recipe.setSugestedTime(DEFAULT_SUGESTED_TIME);
        recipe.setIngredientsInSteps(DEFAULT_INGREDIENTS_IN_STEPS);
        recipe.setSocialEntity(socialEntity);
        recipe.setCreationDate(new DateTime());
        recipe.getSteps().add(step01);
        step01.setRecipe(recipe);

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
        this.friendAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(FRIEND_LOGIN_USER, FRIEND_USER_PASSWORD));
    }

    @Test
    @Transactional
    public void createRecipeWithIngredient() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseRecipeSizeBeforeCreate = recipeService.findAll().size();
        int databaseIngredientSizeBeforeCreate = ingredientService.findAll().size();
        int databaseFoodSizeBeforeCreate = foodService.findAll().size();
        int recipeId;

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[0].ingredients[*].amount",
                Matchers.containsInAnyOrder(ingredient01.getAmount(), ingredient02.getAmount())))
            .andExpect(jsonPath("$steps[0].ingredients", Matchers.hasSize(2)))
            .andExpect(jsonPath("$steps[0].ingredients[*].food.normalizaedName",
                Matchers.containsInAnyOrder(DEFAULT_NORMALIZED_NAME_01, DEFAULT_NORMALIZED_NAME_02)))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        try {
            recipeId = (new ObjectMapper())
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), Recipe.class)
                .getId().intValue();

            restRecipeMockMvc.perform(get("/api/recipes/" + recipeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$steps[0].ingredients[*].amount",
                    Matchers.containsInAnyOrder(ingredient01.getAmount(), ingredient02.getAmount())))
                .andExpect(jsonPath("$steps[0].ingredients", Matchers.hasSize(2)))
                .andExpect(jsonPath("$steps[0].ingredients[*].food.normalizaedName",
                    Matchers.containsInAnyOrder(DEFAULT_NORMALIZED_NAME_01, DEFAULT_NORMALIZED_NAME_02)))
                .andExpect(jsonPath("$name").value(recipe.getName()));

        }catch (Exception e){
            assertThat(false);
        }

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseRecipeSizeBeforeCreate + 1);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        StrictAssertions.assertThat(testRecipe.getName()).isEqualTo(DEFAULT_RECIPE_NAME);
        StrictAssertions.assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        StrictAssertions.assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        StrictAssertions.assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
        StrictAssertions.assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isEqualTo(testRecipe.getUpdateDate());

        // Validate the Recipe in the database
        assertThat(recipeService.findAll()).hasSize(databaseRecipeSizeBeforeCreate+1);
        assertThat(ingredientService.findAll()).hasSize(databaseIngredientSizeBeforeCreate+2);
        assertThat(foodService.findAll()).hasSize(databaseFoodSizeBeforeCreate+2);
    }

    @Test
    @Transactional
    public void updateRecipeWithIngredient() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        recipeService.save(recipe);

        int databaseRecipeSizeBeforeUpdate = recipeService.findAll().size();
        int databaseFoodSizeBeforeUpdate = foodService.findAll().size();
        int recipeId;

        step01.setIngredients(Sets.newHashSet(ingredient02));
        step01.getIngredients().add(ingredient03);

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[0].ingredients[*].amount",
                Matchers.containsInAnyOrder(ingredient02.getAmount(), ingredient03.getAmount())))
            .andExpect(jsonPath("$steps[0].ingredients", Matchers.hasSize(2)))
            .andExpect(jsonPath("$steps[0].ingredients[*].food.normalizaedName",
                Matchers.containsInAnyOrder(DEFAULT_NORMALIZED_NAME_02, food03.getNormalizaedName())))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        try {
            recipeId = (new ObjectMapper())
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), Recipe.class)
                .getId().intValue();

            restRecipeMockMvc.perform(get("/api/recipes/" + recipeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$steps[0].ingredients[*].amount",
                    Matchers.containsInAnyOrder(ingredient02.getAmount(), ingredient03.getAmount())))
                .andExpect(jsonPath("$steps[0].ingredients", Matchers.hasSize(2)))
                .andExpect(jsonPath("$steps[0].ingredients[*].food.normalizaedName",
                    Matchers.containsInAnyOrder(DEFAULT_NORMALIZED_NAME_02, food03.getNormalizaedName())))
                .andExpect(jsonPath("$name").value(recipe.getName()));;

        }catch (Exception e){
            assertThat(false);
        }

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseRecipeSizeBeforeUpdate);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        StrictAssertions.assertThat(testRecipe.getName()).isEqualTo(DEFAULT_RECIPE_NAME);
        StrictAssertions.assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        StrictAssertions.assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        StrictAssertions.assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
        StrictAssertions.assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isLessThan(testRecipe.getUpdateDate());

        // Validate the Recipe in the database
        assertThat(foodService.findAll()).hasSize(databaseFoodSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeWithIngredient() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        recipeService.save(recipe);

        int databaseRecipeSizeBeforeUpdate = recipeService.findAll().size();
        int databaseIngredientSizeBeforeUpdate = ingredientService.findAll().size();
        int databaseFoodSizeBeforeUpdate = foodService.findAll().size();
        int recipeId;

        step01.getIngredients().add(ingredient03);

        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$steps[0].ingredients[*].amount",
                Matchers.containsInAnyOrder(ingredient02.getAmount(), ingredient03.getAmount(),
                    ingredient01.getAmount())))
            .andExpect(jsonPath("$steps[0].ingredients", Matchers.hasSize(3)))
            .andExpect(jsonPath("$steps[0].ingredients[*].food.normalizaedName",
                Matchers.containsInAnyOrder(DEFAULT_NORMALIZED_NAME_02, food03.getNormalizaedName(),
                    food01.getNormalizaedName())))
            .andExpect(jsonPath("$name").value(recipe.getName()));

        try {
            recipeId = (new ObjectMapper())
                .readValue(resultActions.andReturn().getResponse().getContentAsString(), Recipe.class)
                .getId().intValue();

            restRecipeMockMvc.perform(get("/api/recipes/" + recipeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$steps[0].ingredients[*].amount",
                    Matchers.containsInAnyOrder(ingredient02.getAmount(), ingredient03.getAmount(),
                        ingredient01.getAmount())))
                .andExpect(jsonPath("$steps[0].ingredients", Matchers.hasSize(3)))
                .andExpect(jsonPath("$steps[0].ingredients[*].food.normalizaedName",
                    Matchers.containsInAnyOrder(DEFAULT_NORMALIZED_NAME_02, food03.getNormalizaedName(),
                        food01.getNormalizaedName())))
                .andExpect(jsonPath("$name").value(recipe.getName()));;

        }catch (Exception e){
            assertThat(false);
        }

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseRecipeSizeBeforeUpdate + 1);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        StrictAssertions.assertThat(testRecipe.getName()).isEqualTo(DEFAULT_RECIPE_NAME);
        StrictAssertions.assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        StrictAssertions.assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        StrictAssertions.assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate().toDateTime(DateTimeZone.UTC)).isLessThanOrEqualTo(new DateTime());
        StrictAssertions.assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
        StrictAssertions.assertThat(testRecipe.getUser().getLogin()).isEqualTo(FRIEND_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isLessThan(testRecipe.getUpdateDate());

        // Validate the Recipe in the database
        assertThat(foodService.findAll()).hasSize(databaseFoodSizeBeforeUpdate);
        assertThat(ingredientService.findAll()).hasSize(databaseIngredientSizeBeforeUpdate+3);
    }

    @Test
    @Transactional
    public void searchAFoplod() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        recipeService.save(recipe);

        // Search the Food
        restFoodMockMvc.perform(get("/api/foods/search/{name}", StringUtils.stripAccents(food02.getName())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].normalizaedName").value(DEFAULT_NORMALIZED_NAME_02))
            .andExpect(jsonPath("$", Matchers.hasSize(1)));

    }

}
