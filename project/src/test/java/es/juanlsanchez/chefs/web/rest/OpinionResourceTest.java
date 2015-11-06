package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Opinion;
import es.juanlsanchez.chefs.repository.OpinionRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the OpinionResource REST controller.
 *
 * @see OpinionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class OpinionResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final Integer DEFAULT_MIN_VALUE = 1;
    private static final Integer UPDATED_MIN_VALUE = 2;

    private static final Integer DEFAULT_MAXIMUM = 1;
    private static final Integer UPDATED_MAXIMUM = 2;

    @Inject
    private OpinionRepository opinionRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restOpinionMockMvc;

    private Opinion opinion;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OpinionResource opinionResource = new OpinionResource();
        ReflectionTestUtils.setField(opinionResource, "opinionRepository", opinionRepository);
        this.restOpinionMockMvc = MockMvcBuilders.standaloneSetup(opinionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        opinion = new Opinion();
        opinion.setName(DEFAULT_NAME);
        opinion.setMinValue(DEFAULT_MIN_VALUE);
        opinion.setMaximum(DEFAULT_MAXIMUM);
    }

    @Test
    @Transactional
    public void createOpinion() throws Exception {
        int databaseSizeBeforeCreate = opinionRepository.findAll().size();

        // Create the Opinion

        restOpinionMockMvc.perform(post("/api/opinions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(opinion)))
                .andExpect(status().isCreated());

        // Validate the Opinion in the database
        List<Opinion> opinions = opinionRepository.findAll();
        assertThat(opinions).hasSize(databaseSizeBeforeCreate + 1);
        Opinion testOpinion = opinions.get(opinions.size() - 1);
        assertThat(testOpinion.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOpinion.getMinValue()).isEqualTo(DEFAULT_MIN_VALUE);
        assertThat(testOpinion.getMaximum()).isEqualTo(DEFAULT_MAXIMUM);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = opinionRepository.findAll().size();
        // set the field null
        opinion.setName(null);

        // Create the Opinion, which fails.

        restOpinionMockMvc.perform(post("/api/opinions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(opinion)))
                .andExpect(status().isBadRequest());

        List<Opinion> opinions = opinionRepository.findAll();
        assertThat(opinions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMinValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = opinionRepository.findAll().size();
        // set the field null
        opinion.setMinValue(null);

        // Create the Opinion, which fails.

        restOpinionMockMvc.perform(post("/api/opinions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(opinion)))
                .andExpect(status().isBadRequest());

        List<Opinion> opinions = opinionRepository.findAll();
        assertThat(opinions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMaximumIsRequired() throws Exception {
        int databaseSizeBeforeTest = opinionRepository.findAll().size();
        // set the field null
        opinion.setMaximum(null);

        // Create the Opinion, which fails.

        restOpinionMockMvc.perform(post("/api/opinions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(opinion)))
                .andExpect(status().isBadRequest());

        List<Opinion> opinions = opinionRepository.findAll();
        assertThat(opinions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOpinions() throws Exception {
        // Initialize the database
        opinionRepository.saveAndFlush(opinion);

        // Get all the opinions
        restOpinionMockMvc.perform(get("/api/opinions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(opinion.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].minValue").value(hasItem(DEFAULT_MIN_VALUE)))
                .andExpect(jsonPath("$.[*].maximum").value(hasItem(DEFAULT_MAXIMUM)));
    }

    @Test
    @Transactional
    public void getOpinion() throws Exception {
        // Initialize the database
        opinionRepository.saveAndFlush(opinion);

        // Get the opinion
        restOpinionMockMvc.perform(get("/api/opinions/{id}", opinion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(opinion.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.minValue").value(DEFAULT_MIN_VALUE))
            .andExpect(jsonPath("$.maximum").value(DEFAULT_MAXIMUM));
    }

    @Test
    @Transactional
    public void getNonExistingOpinion() throws Exception {
        // Get the opinion
        restOpinionMockMvc.perform(get("/api/opinions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOpinion() throws Exception {
        // Initialize the database
        opinionRepository.saveAndFlush(opinion);

		int databaseSizeBeforeUpdate = opinionRepository.findAll().size();

        // Update the opinion
        opinion.setName(UPDATED_NAME);
        opinion.setMinValue(UPDATED_MIN_VALUE);
        opinion.setMaximum(UPDATED_MAXIMUM);
        

        restOpinionMockMvc.perform(put("/api/opinions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(opinion)))
                .andExpect(status().isOk());

        // Validate the Opinion in the database
        List<Opinion> opinions = opinionRepository.findAll();
        assertThat(opinions).hasSize(databaseSizeBeforeUpdate);
        Opinion testOpinion = opinions.get(opinions.size() - 1);
        assertThat(testOpinion.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOpinion.getMinValue()).isEqualTo(UPDATED_MIN_VALUE);
        assertThat(testOpinion.getMaximum()).isEqualTo(UPDATED_MAXIMUM);
    }

    @Test
    @Transactional
    public void deleteOpinion() throws Exception {
        // Initialize the database
        opinionRepository.saveAndFlush(opinion);

		int databaseSizeBeforeDelete = opinionRepository.findAll().size();

        // Get the opinion
        restOpinionMockMvc.perform(delete("/api/opinions/{id}", opinion.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Opinion> opinions = opinionRepository.findAll();
        assertThat(opinions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
