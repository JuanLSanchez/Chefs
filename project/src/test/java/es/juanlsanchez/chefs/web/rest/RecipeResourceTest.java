package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.service.RecipeService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the RecipeResource REST controller.
 *
 * @see RecipeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class RecipeResourceTest {

    private static final String DEFAULT_SEARCH = "recipe";

    private static final String DEFAULT_NAME = DEFAULT_SEARCH+"SAMPLE_TEXT";
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

    private static final String FRIEND_LOGIN_USER = "user001";
    private static final String FRIEND_USER_PASSWORD = "user";

    private static final String DEFAULT_LOGIN_USER = "user002";
    private static final String DEFAULT_USER_PASSWORD = "user";

    private static final String SECOND_LOGIN_USER = "user";
    private static final String SECOND_USER_PASSWORD = "user";

    private static final String BLOCKED_LOGIN_USER = "user004";
    private static final String IGNORED_LOGIN_USER = "user008";

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

    private Recipe recipe, secondRecipe;

    private Authentication authentication, friendAuthentication, secondAuthentication,
        blockedAuthentication, ignoredAtuhtentication;

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

        secondRecipe = new Recipe();
        secondRecipe.setName(DEFAULT_NAME);
        secondRecipe.setDescription(DEFAULT_DESCRIPTION);
        secondRecipe.setInformationUrl(DEFAULT_INFORMATION_URL);
        secondRecipe.setAdvice(DEFAULT_ADVICE);
        secondRecipe.setSugestedTime(DEFAULT_SUGESTED_TIME);
        secondRecipe.setIngredientsInSteps(DEFAULT_INGREDIENTS_IN_STEPS);
        secondRecipe.setSocialEntity(secondSocialEntity);
        secondRecipe.setCreationDate(new DateTime());

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
        this.friendAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(FRIEND_LOGIN_USER, FRIEND_USER_PASSWORD));
        this.secondAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(SECOND_LOGIN_USER, SECOND_USER_PASSWORD));
        this.blockedAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(BLOCKED_LOGIN_USER, DEFAULT_USER_PASSWORD));
        this.ignoredAtuhtentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(IGNORED_LOGIN_USER, DEFAULT_USER_PASSWORD));

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
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isEqualTo(testRecipe.getUpdateDate());
    }

    @Test
    @Transactional
    public void createRecipeAsOtherUser() throws Exception {
        // Create the Recipe with the default user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        secondRecipe.setUser(recipeService.save(recipe).getUser());

        // Try to create the recipe with the second user as default user
        int databaseSizeBeforeCreate = recipeService.findAll().size();
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(this.secondRecipe));
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
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(SECOND_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isEqualTo(testRecipe.getUpdateDate());
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
    public void getRecipe() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

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
    public void getRecipeNotPublic() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

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
    public void getRecipeBlocked() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

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
    public void getRecipeAsOtherUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);
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
    public void getRecipeNotPublicAsOtherUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getRecipeBlockedAsOtherUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getRecipeAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.clearContext();
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
    public void getRecipeNotPublicAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getRecipeBlockedAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getRecipeAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
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
    public void getRecipeNotPublicAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getRecipeBlockedAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getRecipeAsIgnoredUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.ignoredAtuhtentication);
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
    public void getRecipeNotPublicAsIgnoredUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.ignoredAtuhtentication);
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getRecipeBlockedAsIgnoredUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.ignoredAtuhtentication);
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getRecipeAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
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
    public void getRecipeNotPublicAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
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
    public void getRecipeBlockedAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipe as other user
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getNonExistingRecipe() throws Exception {
        //Login
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void findAllRecipesByPrincipal() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/user", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByPrincipalNotClonable() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/user", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByPrincipalAndNotPublic() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/user", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByPrincipalAndBlocked() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/user", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLogin() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginNotClonable() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAndNotPublic() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAndBlocked() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginNotClonableAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAndNotPublicAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue()+1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAndBlockedAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginNotClonableAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAndNotPublicAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAndBlockedAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAsAnonymousUser() throws Exception {
        // Initialize the database
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginNotClonableAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.clearContext();
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAndNotPublicAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.clearContext();
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesByLoginAndBlockedAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.clearContext();
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllByLoginAndIsVisibility(DEFAULT_LOGIN_USER, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes_dto/user/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeName() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameNotClonable() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAndNotPublic() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAndBlocked() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameNotClonableAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAndNotPublicAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue()+1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAndBlockedAsFriendUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameNotClonableAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAndNotPublicAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAndBlockedAsBlockedUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.clearContext();
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameNotClonableAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.clearContext();
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue() + 1)))
            .andExpect(jsonPath("$[0].id").value(recipe.getId().intValue()));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAndNotPublicAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.clearContext();
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void findAllRecipesLikeNameAndBlockedAsAnonymousUser() throws Exception {
        // Initialize the database
        SecurityContextHolder.clearContext();
        Long databaseSizeBeforeUpdate = recipeService
            .findDTOAllIsVisibilityAndLikeName(DEFAULT_SEARCH, new PageRequest(0, 1))
            .getTotalElements();

        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

        // Get the recipes
        SecurityContextHolder.clearContext();
        restRecipeMockMvc.perform(get("/api/recipes_dto/findAllIsVisibilityAndLikeName/{name}", DEFAULT_SEARCH))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(databaseSizeBeforeUpdate.intValue())));
    }

    @Test
    @Transactional
    public void updateRecipe() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipeService.save(recipe);

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
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isLessThan(testRecipe.getUpdateDate());
    }

    @Test
    @Transactional
    public void updateRecipeNotPublic() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);
        recipeService.save(recipe);

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
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isLessThan(testRecipe.getUpdateDate());
    }

    @Test
    @Transactional
    public void updateRecipeBlocked() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipeService.save(recipe);

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
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isLessThan(testRecipe.getUpdateDate());
    }

    @Test
    @Transactional
    public void updateRecipeAllBlocked() throws Exception {
        // Initialize the database
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);
        recipe.getSocialEntity().setIsPublic(false);
        recipe.getSocialEntity().setPublicInscription(false);
        recipeService.save(recipe);

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
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
        assertThat(testRecipe.getCreationDate()).isLessThan(testRecipe.getUpdateDate());
    }

    @Test
    @Transactional
    public void cloneRecipe() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        long fatherId = recipeService.save(recipe).getId();

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);
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
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate + 1);
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
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(SECOND_LOGIN_USER);
        assertThat(testRecipe.getFather().getId()).isEqualTo(fatherId);
    }

    @Test
    @Transactional
    public void cloneRecipeNotPublicAsFollowerUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);

        long fatherId = recipeService.save(recipe).getId();

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
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
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate+1);
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
        assertThat(testRecipe.getUser().getLogin()).isEqualTo(FRIEND_LOGIN_USER);
        assertThat(testRecipe.getFather().getId()).isEqualTo(fatherId);
    }

    @Test
    @Transactional
    public void cloneRecipeNotPublicAsBlockedUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        this.recipe.getSocialEntity().setIsPublic(false);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeNotPublicAsIgnoredUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.ignoredAtuhtentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeNotPublicAsOtherUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setIsPublic(false);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeNotClonableAsFollowerUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeNotClonableAsBlockedUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeNotClonableAsIgnoredUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.ignoredAtuhtentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeNotClonableAsOtherUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setPublicInscription(false);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeBlockedAsFollowerUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.friendAuthentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeBlockedAsBlockedUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.blockedAuthentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeBlockedAsIgnoredUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.ignoredAtuhtentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void cloneRecipeBlockedAsOtherUser() throws Exception {
        // Initialize the database with a user
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        recipe.getSocialEntity().setBlocked(true);

        recipeService.save(recipe);

        int databaseSizeBeforeUpdate = recipeService.findAll().size();

        // Update the recipe with other user
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);

        restRecipeMockMvc.perform(put("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe)))
            .andExpect(status().isBadRequest());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeService.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
    }
}
