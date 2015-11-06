package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.repository.RecipeRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.LocalDate;
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

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_CREATION_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_CREATION_DATE = new LocalDate();
    private static final String DEFAULT_INFORMATION_URL = "SAMPLE_TEXT";
    private static final String UPDATED_INFORMATION_URL = "UPDATED_TEXT";
    private static final String DEFAULT_ADVICE = "SAMPLE_TEXT";
    private static final String UPDATED_ADVICE = "UPDATED_TEXT";
    private static final String DEFAULT_SUGESTED_TIME = "SAMPLE_TEXT";
    private static final String UPDATED_SUGESTED_TIME = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_UPDATE_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = new LocalDate();

    private static final Boolean DEFAULT_INGREDIENTS_IN_STEPS = false;
    private static final Boolean UPDATED_INGREDIENTS_IN_STEPS = true;

    @Inject
    private RecipeRepository recipeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRecipeMockMvc;

    private Recipe recipe;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RecipeResource recipeResource = new RecipeResource();
        ReflectionTestUtils.setField(recipeResource, "recipeRepository", recipeRepository);
        this.restRecipeMockMvc = MockMvcBuilders.standaloneSetup(recipeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        recipe = new Recipe();
        recipe.setName(DEFAULT_NAME);
        recipe.setDescription(DEFAULT_DESCRIPTION);
        recipe.setCreationDate(DEFAULT_CREATION_DATE);
        recipe.setInformationUrl(DEFAULT_INFORMATION_URL);
        recipe.setAdvice(DEFAULT_ADVICE);
        recipe.setSugestedTime(DEFAULT_SUGESTED_TIME);
        recipe.setUpdateDate(DEFAULT_UPDATE_DATE);
        recipe.setIngredientsInSteps(DEFAULT_INGREDIENTS_IN_STEPS);
    }

    @Test
    @Transactional
    public void createRecipe() throws Exception {
        int databaseSizeBeforeCreate = recipeRepository.findAll().size();

        // Create the Recipe

        restRecipeMockMvc.perform(post("/api/recipes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isCreated());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeCreate + 1);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRecipe.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testRecipe.getInformationUrl()).isEqualTo(DEFAULT_INFORMATION_URL);
        assertThat(testRecipe.getAdvice()).isEqualTo(DEFAULT_ADVICE);
        assertThat(testRecipe.getSugestedTime()).isEqualTo(DEFAULT_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
        assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(DEFAULT_INGREDIENTS_IN_STEPS);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeRepository.findAll().size();
        // set the field null
        recipe.setName(null);

        // Create the Recipe, which fails.

        restRecipeMockMvc.perform(post("/api/recipes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isBadRequest());

        List<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeRepository.findAll().size();
        // set the field null
        recipe.setDescription(null);

        // Create the Recipe, which fails.

        restRecipeMockMvc.perform(post("/api/recipes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isBadRequest());

        List<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = recipeRepository.findAll().size();
        // set the field null
        recipe.setCreationDate(null);

        // Create the Recipe, which fails.

        restRecipeMockMvc.perform(post("/api/recipes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isBadRequest());

        List<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecipes() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get all the recipes
        restRecipeMockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
                .andExpect(jsonPath("$.[*].informationUrl").value(hasItem(DEFAULT_INFORMATION_URL.toString())))
                .andExpect(jsonPath("$.[*].advice").value(hasItem(DEFAULT_ADVICE.toString())))
                .andExpect(jsonPath("$.[*].sugestedTime").value(hasItem(DEFAULT_SUGESTED_TIME.toString())))
                .andExpect(jsonPath("$.[*].updateDate").value(hasItem(DEFAULT_UPDATE_DATE.toString())))
                .andExpect(jsonPath("$.[*].ingredientsInSteps").value(hasItem(DEFAULT_INGREDIENTS_IN_STEPS.booleanValue())));
    }

    @Test
    @Transactional
    public void getRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", recipe.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(recipe.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.informationUrl").value(DEFAULT_INFORMATION_URL.toString()))
            .andExpect(jsonPath("$.advice").value(DEFAULT_ADVICE.toString()))
            .andExpect(jsonPath("$.sugestedTime").value(DEFAULT_SUGESTED_TIME.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()))
            .andExpect(jsonPath("$.ingredientsInSteps").value(DEFAULT_INGREDIENTS_IN_STEPS.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRecipe() throws Exception {
        // Get the recipe
        restRecipeMockMvc.perform(get("/api/recipes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

		int databaseSizeBeforeUpdate = recipeRepository.findAll().size();

        // Update the recipe
        recipe.setName(UPDATED_NAME);
        recipe.setDescription(UPDATED_DESCRIPTION);
        recipe.setCreationDate(UPDATED_CREATION_DATE);
        recipe.setInformationUrl(UPDATED_INFORMATION_URL);
        recipe.setAdvice(UPDATED_ADVICE);
        recipe.setSugestedTime(UPDATED_SUGESTED_TIME);
        recipe.setUpdateDate(UPDATED_UPDATE_DATE);
        recipe.setIngredientsInSteps(UPDATED_INGREDIENTS_IN_STEPS);
        

        restRecipeMockMvc.perform(put("/api/recipes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(recipe)))
                .andExpect(status().isOk());

        // Validate the Recipe in the database
        List<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeUpdate);
        Recipe testRecipe = recipes.get(recipes.size() - 1);
        assertThat(testRecipe.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecipe.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRecipe.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testRecipe.getInformationUrl()).isEqualTo(UPDATED_INFORMATION_URL);
        assertThat(testRecipe.getAdvice()).isEqualTo(UPDATED_ADVICE);
        assertThat(testRecipe.getSugestedTime()).isEqualTo(UPDATED_SUGESTED_TIME);
        assertThat(testRecipe.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);
        assertThat(testRecipe.getIngredientsInSteps()).isEqualTo(UPDATED_INGREDIENTS_IN_STEPS);
    }

    @Test
    @Transactional
    public void deleteRecipe() throws Exception {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe);

		int databaseSizeBeforeDelete = recipeRepository.findAll().size();

        // Get the recipe
        restRecipeMockMvc.perform(delete("/api/recipes/{id}", recipe.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Recipe> recipes = recipeRepository.findAll();
        assertThat(recipes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
