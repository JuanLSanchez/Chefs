package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.Tag;
import es.juanlsanchez.chefs.service.RecipeService;
import es.juanlsanchez.chefs.service.TagService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TagResource REST controller.
 *
 * @see TagResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TagResourceTest {

    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
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

    @Inject
    private TagService tagService;

    @Inject
    private RecipeService recipeService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private ApplicationContext context;

    private MockMvc restTagMockMvc, restRecipeMockMvc;

    private Tag tag;
    private Recipe recipe;

    private Authentication authentication;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TagResource tagResource = new TagResource();
        ReflectionTestUtils.setField(tagResource, "tagService", tagService);
        this.restTagMockMvc = MockMvcBuilders.standaloneSetup(tagResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
        RecipeResource recipeResource = new RecipeResource();
        ReflectionTestUtils.setField(recipeResource, "recipeService", recipeService);
        this.restRecipeMockMvc = MockMvcBuilders.standaloneSetup(recipeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        tag = new Tag();
        tag.setName(DEFAULT_NAME);

        SocialEntity socialEntity = new SocialEntity();
        socialEntity.setBlocked(DEFAULT_SOCIAL_ENTITY_BLOCKED);
        socialEntity.setIsPublic(DEFAULT_SOCIAL_ENTITY_IS_PUBLIC);
        socialEntity.setPublicInscription(DEFAULT_SOCIAL_ENTITY_PUBLIC_INSCRIPTION);
        socialEntity.getTags().add(tag);

        recipe = new Recipe();
        recipe.setName(DEFAULT_NAME);
        recipe.setDescription(DEFAULT_DESCRIPTION);
        recipe.setInformationUrl(DEFAULT_INFORMATION_URL);
        recipe.setAdvice(DEFAULT_ADVICE);
        recipe.setSugestedTime(DEFAULT_SUGESTED_TIME);
        recipe.setIngredientsInSteps(DEFAULT_INGREDIENTS_IN_STEPS);
        recipe.setSocialEntity(socialEntity);
        recipe.setCreationDate(new DateTime());

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
    }

    @Test
    @Transactional
    public void createRecipeWithTag() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        int databaseSizeBeforeCreate = recipeService.findAll().size();
        Long tagsSizeBeforeCreate = tagService
            .findAllByNameContains(DEFAULT_NAME, new PageRequest(0, 1))
            .getTotalElements();

        // Create the Recipe
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = post("/api/recipes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(recipe));
        ResultActions resultActions = restRecipeMockMvc.perform(mockHttpServletRequestBuilder);
        resultActions
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$socialEntity.tags[0].name").value(tag.getName()))
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

        // Get all the tags
        restTagMockMvc.perform(get("/api/tags/byNameContains/{name}", tag.getName()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(tagsSizeBeforeCreate.intValue() + 1)));
    }
}
