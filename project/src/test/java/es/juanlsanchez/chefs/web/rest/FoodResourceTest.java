package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.TestConstants;
import es.juanlsanchez.chefs.domain.Food;
import es.juanlsanchez.chefs.repository.FoodRepository;

import org.junit.Before;
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


/**
 * Test class for the FoodResource REST controller.
 *
 * @see FoodResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FoodResourceTest {

    private static final String DEFAULT_NORMALIZAED_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NORMALIZAED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Double DEFAULT_KCAL = 0D;
    private static final Double UPDATED_KCAL = 1D;

    @Inject
    private FoodRepository foodRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFoodMockMvc;

    private Food food;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FoodResource foodResource = new FoodResource();
        ReflectionTestUtils.setField(foodResource, "foodRepository", foodRepository);
        this.restFoodMockMvc = MockMvcBuilders.standaloneSetup(foodResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        food = new Food();
        food.setNormalizaedName(DEFAULT_NORMALIZAED_NAME);
        food.setName(DEFAULT_NAME);
        food.setKcal(DEFAULT_KCAL);
    }

    @Test
    @Transactional
    public void createFood() throws Exception {
        int databaseSizeBeforeCreate = foodRepository.findAll().size();

        // Create the Food

        restFoodMockMvc.perform(post("/api/foods")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(food)))
                .andExpect(status().isCreated());

        // Validate the Food in the database
        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(databaseSizeBeforeCreate + 1);
        Food testFood = foods.get(foods.size() - 1);
        assertThat(testFood.getNormalizaedName()).isEqualTo(DEFAULT_NORMALIZAED_NAME);
        assertThat(testFood.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFood.getKcal()).isEqualTo(DEFAULT_KCAL);
    }

    @Test
    @Transactional
    public void checkNormalizaedNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = foodRepository.findAll().size();
        // set the field null
        food.setNormalizaedName(null);

        // Create the Food, which fails.

        restFoodMockMvc.perform(post("/api/foods")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(food)))
                .andExpect(status().isBadRequest());

        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = foodRepository.findAll().size();
        // set the field null
        food.setName(null);

        // Create the Food, which fails.

        restFoodMockMvc.perform(post("/api/foods")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(food)))
                .andExpect(status().isBadRequest());

        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllFoods() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the foods
        restFoodMockMvc.perform(get("/api/foods"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(food.getId().intValue())))
                .andExpect(jsonPath("$.[*].normalizaedName").value(hasItem(DEFAULT_NORMALIZAED_NAME.toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].kcal").value(hasItem(DEFAULT_KCAL.doubleValue())));
    }

    @Test
    @Transactional
    public void getFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

        // Get the food
        restFoodMockMvc.perform(get("/api/foods/{id}", food.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(food.getId().intValue()))
            .andExpect(jsonPath("$.normalizaedName").value(DEFAULT_NORMALIZAED_NAME.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.kcal").value(DEFAULT_KCAL.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFood() throws Exception {
        // Get the food
        restFoodMockMvc.perform(get("/api/foods/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

		int databaseSizeBeforeUpdate = foodRepository.findAll().size();

        // Update the food
        food.setNormalizaedName(UPDATED_NORMALIZAED_NAME);
        food.setName(UPDATED_NAME);
        food.setKcal(UPDATED_KCAL);


        restFoodMockMvc.perform(put("/api/foods")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(food)))
                .andExpect(status().isOk());

        // Validate the Food in the database
        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(databaseSizeBeforeUpdate);
        Food testFood = foods.get(foods.size() - 1);
        assertThat(testFood.getNormalizaedName()).isEqualTo(UPDATED_NORMALIZAED_NAME);
        assertThat(testFood.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFood.getKcal()).isEqualTo(UPDATED_KCAL);
    }

    @Test
    @Transactional
    public void deleteFood() throws Exception {
        // Initialize the database
        foodRepository.saveAndFlush(food);

		int databaseSizeBeforeDelete = foodRepository.findAll().size();

        // Get the food
        restFoodMockMvc.perform(delete("/api/foods/{id}", food.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Food> foods = foodRepository.findAll();
        assertThat(foods).hasSize(databaseSizeBeforeDelete - 1);
    }
}
