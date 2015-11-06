package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Assessment;
import es.juanlsanchez.chefs.repository.AssessmentRepository;

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
 * Test class for the AssessmentResource REST controller.
 *
 * @see AssessmentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AssessmentResourceTest {


    private static final Integer DEFAULT_RATING = 0;
    private static final Integer UPDATED_RATING = 1;

    @Inject
    private AssessmentRepository assessmentRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAssessmentMockMvc;

    private Assessment assessment;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AssessmentResource assessmentResource = new AssessmentResource();
        ReflectionTestUtils.setField(assessmentResource, "assessmentRepository", assessmentRepository);
        this.restAssessmentMockMvc = MockMvcBuilders.standaloneSetup(assessmentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        assessment = new Assessment();
        assessment.setRating(DEFAULT_RATING);
    }

    @Test
    @Transactional
    public void createAssessment() throws Exception {
        int databaseSizeBeforeCreate = assessmentRepository.findAll().size();

        // Create the Assessment

        restAssessmentMockMvc.perform(post("/api/assessments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(assessment)))
                .andExpect(status().isCreated());

        // Validate the Assessment in the database
        List<Assessment> assessments = assessmentRepository.findAll();
        assertThat(assessments).hasSize(databaseSizeBeforeCreate + 1);
        Assessment testAssessment = assessments.get(assessments.size() - 1);
        assertThat(testAssessment.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    @Transactional
    public void checkRatingIsRequired() throws Exception {
        int databaseSizeBeforeTest = assessmentRepository.findAll().size();
        // set the field null
        assessment.setRating(null);

        // Create the Assessment, which fails.

        restAssessmentMockMvc.perform(post("/api/assessments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(assessment)))
                .andExpect(status().isBadRequest());

        List<Assessment> assessments = assessmentRepository.findAll();
        assertThat(assessments).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAssessments() throws Exception {
        // Initialize the database
        assessmentRepository.saveAndFlush(assessment);

        // Get all the assessments
        restAssessmentMockMvc.perform(get("/api/assessments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(assessment.getId().intValue())))
                .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)));
    }

    @Test
    @Transactional
    public void getAssessment() throws Exception {
        // Initialize the database
        assessmentRepository.saveAndFlush(assessment);

        // Get the assessment
        restAssessmentMockMvc.perform(get("/api/assessments/{id}", assessment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(assessment.getId().intValue()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING));
    }

    @Test
    @Transactional
    public void getNonExistingAssessment() throws Exception {
        // Get the assessment
        restAssessmentMockMvc.perform(get("/api/assessments/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAssessment() throws Exception {
        // Initialize the database
        assessmentRepository.saveAndFlush(assessment);

		int databaseSizeBeforeUpdate = assessmentRepository.findAll().size();

        // Update the assessment
        assessment.setRating(UPDATED_RATING);
        

        restAssessmentMockMvc.perform(put("/api/assessments")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(assessment)))
                .andExpect(status().isOk());

        // Validate the Assessment in the database
        List<Assessment> assessments = assessmentRepository.findAll();
        assertThat(assessments).hasSize(databaseSizeBeforeUpdate);
        Assessment testAssessment = assessments.get(assessments.size() - 1);
        assertThat(testAssessment.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    public void deleteAssessment() throws Exception {
        // Initialize the database
        assessmentRepository.saveAndFlush(assessment);

		int databaseSizeBeforeDelete = assessmentRepository.findAll().size();

        // Get the assessment
        restAssessmentMockMvc.perform(delete("/api/assessments/{id}", assessment.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Assessment> assessments = assessmentRepository.findAll();
        assertThat(assessments).hasSize(databaseSizeBeforeDelete - 1);
    }
}
