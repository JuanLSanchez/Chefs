package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.TestConstants;
import es.juanlsanchez.chefs.domain.Ingredient;
import es.juanlsanchez.chefs.repository.IngredientRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import es.juanlsanchez.chefs.domain.enumeration.Measurement;

/**
 * Test class for the IngredientResource REST controller.
 *
 * @see IngredientResource
 */
/** TODO: Make tests */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Ignore
public class IngredientResourceTest {


    private static final Double DEFAULT_AMOUNT = 0D;
    private static final Double UPDATED_AMOUNT = 1D;

    private static final Measurement DEFAULT_MEASUREMENT = Measurement.g;
    private static final Measurement UPDATED_MEASUREMENT = Measurement.kg;

    @Inject
    private IngredientRepository ingredientRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restIngredientMockMvc;

    private Ingredient ingredient;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        IngredientResource ingredientResource = new IngredientResource();
        ReflectionTestUtils.setField(ingredientResource, "ingredientRepository", ingredientRepository);
        this.restIngredientMockMvc = MockMvcBuilders.standaloneSetup(ingredientResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        ingredient = new Ingredient();
        ingredient.setAmount(DEFAULT_AMOUNT);
        ingredient.setMeasurement(DEFAULT_MEASUREMENT);
    }

    @Test
    @Transactional
    public void createIngredient() throws Exception {
        int databaseSizeBeforeCreate = ingredientRepository.findAll().size();

        // Create the Ingredient

        restIngredientMockMvc.perform(post("/api/ingredients")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ingredient)))
                .andExpect(status().isCreated());

        // Validate the Ingredient in the database
        List<Ingredient> ingredients = ingredientRepository.findAll();
        assertThat(ingredients).hasSize(databaseSizeBeforeCreate + 1);
        Ingredient testIngredient = ingredients.get(ingredients.size() - 1);
        assertThat(testIngredient.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testIngredient.getMeasurement()).isEqualTo(DEFAULT_MEASUREMENT);
    }

    @Test
    @Transactional
    public void getAllIngredients() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the ingredients
        restIngredientMockMvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(ingredient.getId().intValue())))
                .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
                .andExpect(jsonPath("$.[*].measurement").value(hasItem(DEFAULT_MEASUREMENT.toString())));
    }

    @Test
    @Transactional
    public void getIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

        // Get the ingredient
        restIngredientMockMvc.perform(get("/api/ingredients/{id}", ingredient.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(ingredient.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.measurement").value(DEFAULT_MEASUREMENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingIngredient() throws Exception {
        // Get the ingredient
        restIngredientMockMvc.perform(get("/api/ingredients/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

		int databaseSizeBeforeUpdate = ingredientRepository.findAll().size();

        // Update the ingredient
        ingredient.setAmount(UPDATED_AMOUNT);
        ingredient.setMeasurement(UPDATED_MEASUREMENT);


        restIngredientMockMvc.perform(put("/api/ingredients")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(ingredient)))
                .andExpect(status().isOk());

        // Validate the Ingredient in the database
        List<Ingredient> ingredients = ingredientRepository.findAll();
        assertThat(ingredients).hasSize(databaseSizeBeforeUpdate);
        Ingredient testIngredient = ingredients.get(ingredients.size() - 1);
        assertThat(testIngredient.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testIngredient.getMeasurement()).isEqualTo(UPDATED_MEASUREMENT);
    }

    @Test
    @Transactional
    public void deleteIngredient() throws Exception {
        // Initialize the database
        ingredientRepository.saveAndFlush(ingredient);

		int databaseSizeBeforeDelete = ingredientRepository.findAll().size();

        // Get the ingredient
        restIngredientMockMvc.perform(delete("/api/ingredients/{id}", ingredient.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Ingredient> ingredients = ingredientRepository.findAll();
        assertThat(ingredients).hasSize(databaseSizeBeforeDelete - 1);
    }
}
