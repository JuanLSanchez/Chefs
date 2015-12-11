package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Step;
import es.juanlsanchez.chefs.repository.StepRepository;

import org.junit.Before;
import org.junit.Ignore;
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
 * Test class for the StepResource REST controller.
 *
 * @see StepResource
 */
/** TODO: Make tests */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Ignore
public class StepResourceTest {


    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;
    private static final String DEFAULT_SECTION = "SAMPLE_TEXT";
    private static final String UPDATED_SECTION = "UPDATED_TEXT";

    @Inject
    private StepRepository stepRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restStepMockMvc;

    private Step step;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StepResource stepResource = new StepResource();
        ReflectionTestUtils.setField(stepResource, "stepRepository", stepRepository);
        this.restStepMockMvc = MockMvcBuilders.standaloneSetup(stepResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        step = new Step();
        step.setPosition(DEFAULT_POSITION);
        step.setSection(DEFAULT_SECTION);
    }

    @Test
    @Transactional
    public void createStep() throws Exception {
        int databaseSizeBeforeCreate = stepRepository.findAll().size();

        // Create the Step

        restStepMockMvc.perform(post("/api/steps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(step)))
                .andExpect(status().isCreated());

        // Validate the Step in the database
        List<Step> steps = stepRepository.findAll();
        assertThat(steps).hasSize(databaseSizeBeforeCreate + 1);
        Step testStep = steps.get(steps.size() - 1);
        assertThat(testStep.getPosition()).isEqualTo(DEFAULT_POSITION);
        assertThat(testStep.getSection()).isEqualTo(DEFAULT_SECTION);
    }

    @Test
    @Transactional
    public void checkSectionIsRequired() throws Exception {
        int databaseSizeBeforeTest = stepRepository.findAll().size();
        // set the field null
        step.setSection(null);

        // Create the Step, which fails.

        restStepMockMvc.perform(post("/api/steps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(step)))
                .andExpect(status().isBadRequest());

        List<Step> steps = stepRepository.findAll();
        assertThat(steps).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSteps() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);

        // Get all the steps
        restStepMockMvc.perform(get("/api/steps"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(step.getId().intValue())))
                .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION)))
                .andExpect(jsonPath("$.[*].section").value(hasItem(DEFAULT_SECTION.toString())));
    }

    @Test
    @Transactional
    public void getStep() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);

        // Get the step
        restStepMockMvc.perform(get("/api/steps/{id}", step.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(step.getId().intValue()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION))
            .andExpect(jsonPath("$.section").value(DEFAULT_SECTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStep() throws Exception {
        // Get the step
        restStepMockMvc.perform(get("/api/steps/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStep() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);

		int databaseSizeBeforeUpdate = stepRepository.findAll().size();

        // Update the step
        step.setPosition(UPDATED_POSITION);
        step.setSection(UPDATED_SECTION);


        restStepMockMvc.perform(put("/api/steps")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(step)))
                .andExpect(status().isOk());

        // Validate the Step in the database
        List<Step> steps = stepRepository.findAll();
        assertThat(steps).hasSize(databaseSizeBeforeUpdate);
        Step testStep = steps.get(steps.size() - 1);
        assertThat(testStep.getPosition()).isEqualTo(UPDATED_POSITION);
        assertThat(testStep.getSection()).isEqualTo(UPDATED_SECTION);
    }

    @Test
    @Transactional
    public void deleteStep() throws Exception {
        // Initialize the database
        stepRepository.saveAndFlush(step);

		int databaseSizeBeforeDelete = stepRepository.findAll().size();

        // Get the step
        restStepMockMvc.perform(delete("/api/steps/{id}", step.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Step> steps = stepRepository.findAll();
        assertThat(steps).hasSize(databaseSizeBeforeDelete - 1);
    }
}
