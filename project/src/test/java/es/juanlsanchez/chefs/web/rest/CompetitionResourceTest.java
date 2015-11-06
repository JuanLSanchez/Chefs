package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Competition;
import es.juanlsanchez.chefs.repository.CompetitionRepository;

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
 * Test class for the CompetitionResource REST controller.
 *
 * @see CompetitionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CompetitionResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_DEADLINE = new LocalDate(0L);
    private static final LocalDate UPDATED_DEADLINE = new LocalDate();
    private static final String DEFAULT_RULES = "SAMPLE_TEXT";
    private static final String UPDATED_RULES = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_INSCRIPTION_TIME = new LocalDate(0L);
    private static final LocalDate UPDATED_INSCRIPTION_TIME = new LocalDate();

    private static final Integer DEFAULT_MAX_NRECIPES_BY_CHEFS = 0;
    private static final Integer UPDATED_MAX_NRECIPES_BY_CHEFS = 1;

    private static final LocalDate DEFAULT_CREATION_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_CREATION_DATE = new LocalDate();

    private static final Boolean DEFAULT_COMPLETED_SCORE = false;
    private static final Boolean UPDATED_COMPLETED_SCORE = true;

    private static final Boolean DEFAULT_PUBLIC_JURY = false;
    private static final Boolean UPDATED_PUBLIC_JURY = true;

    @Inject
    private CompetitionRepository competitionRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restCompetitionMockMvc;

    private Competition competition;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CompetitionResource competitionResource = new CompetitionResource();
        ReflectionTestUtils.setField(competitionResource, "competitionRepository", competitionRepository);
        this.restCompetitionMockMvc = MockMvcBuilders.standaloneSetup(competitionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        competition = new Competition();
        competition.setName(DEFAULT_NAME);
        competition.setDescription(DEFAULT_DESCRIPTION);
        competition.setDeadline(DEFAULT_DEADLINE);
        competition.setRules(DEFAULT_RULES);
        competition.setInscriptionTime(DEFAULT_INSCRIPTION_TIME);
        competition.setMaxNRecipesByChefs(DEFAULT_MAX_NRECIPES_BY_CHEFS);
        competition.setCreationDate(DEFAULT_CREATION_DATE);
        competition.setCompletedScore(DEFAULT_COMPLETED_SCORE);
        competition.setPublicJury(DEFAULT_PUBLIC_JURY);
    }

    @Test
    @Transactional
    public void createCompetition() throws Exception {
        int databaseSizeBeforeCreate = competitionRepository.findAll().size();

        // Create the Competition

        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isCreated());

        // Validate the Competition in the database
        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeCreate + 1);
        Competition testCompetition = competitions.get(competitions.size() - 1);
        assertThat(testCompetition.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompetition.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testCompetition.getDeadline()).isEqualTo(DEFAULT_DEADLINE);
        assertThat(testCompetition.getRules()).isEqualTo(DEFAULT_RULES);
        assertThat(testCompetition.getInscriptionTime()).isEqualTo(DEFAULT_INSCRIPTION_TIME);
        assertThat(testCompetition.getMaxNRecipesByChefs()).isEqualTo(DEFAULT_MAX_NRECIPES_BY_CHEFS);
        assertThat(testCompetition.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testCompetition.getCompletedScore()).isEqualTo(DEFAULT_COMPLETED_SCORE);
        assertThat(testCompetition.getPublicJury()).isEqualTo(DEFAULT_PUBLIC_JURY);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = competitionRepository.findAll().size();
        // set the field null
        competition.setName(null);

        // Create the Competition, which fails.

        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isBadRequest());

        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = competitionRepository.findAll().size();
        // set the field null
        competition.setDescription(null);

        // Create the Competition, which fails.

        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isBadRequest());

        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDeadlineIsRequired() throws Exception {
        int databaseSizeBeforeTest = competitionRepository.findAll().size();
        // set the field null
        competition.setDeadline(null);

        // Create the Competition, which fails.

        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isBadRequest());

        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkRulesIsRequired() throws Exception {
        int databaseSizeBeforeTest = competitionRepository.findAll().size();
        // set the field null
        competition.setRules(null);

        // Create the Competition, which fails.

        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isBadRequest());

        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkInscriptionTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = competitionRepository.findAll().size();
        // set the field null
        competition.setInscriptionTime(null);

        // Create the Competition, which fails.

        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isBadRequest());

        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMaxNRecipesByChefsIsRequired() throws Exception {
        int databaseSizeBeforeTest = competitionRepository.findAll().size();
        // set the field null
        competition.setMaxNRecipesByChefs(null);

        // Create the Competition, which fails.

        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isBadRequest());

        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCompetitions() throws Exception {
        // Initialize the database
        competitionRepository.saveAndFlush(competition);

        // Get all the competitions
        restCompetitionMockMvc.perform(get("/api/competitions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(competition.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].deadline").value(hasItem(DEFAULT_DEADLINE.toString())))
                .andExpect(jsonPath("$.[*].rules").value(hasItem(DEFAULT_RULES.toString())))
                .andExpect(jsonPath("$.[*].inscriptionTime").value(hasItem(DEFAULT_INSCRIPTION_TIME.toString())))
                .andExpect(jsonPath("$.[*].maxNRecipesByChefs").value(hasItem(DEFAULT_MAX_NRECIPES_BY_CHEFS)))
                .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
                .andExpect(jsonPath("$.[*].completedScore").value(hasItem(DEFAULT_COMPLETED_SCORE.booleanValue())))
                .andExpect(jsonPath("$.[*].publicJury").value(hasItem(DEFAULT_PUBLIC_JURY.booleanValue())));
    }

    @Test
    @Transactional
    public void getCompetition() throws Exception {
        // Initialize the database
        competitionRepository.saveAndFlush(competition);

        // Get the competition
        restCompetitionMockMvc.perform(get("/api/competitions/{id}", competition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(competition.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.deadline").value(DEFAULT_DEADLINE.toString()))
            .andExpect(jsonPath("$.rules").value(DEFAULT_RULES.toString()))
            .andExpect(jsonPath("$.inscriptionTime").value(DEFAULT_INSCRIPTION_TIME.toString()))
            .andExpect(jsonPath("$.maxNRecipesByChefs").value(DEFAULT_MAX_NRECIPES_BY_CHEFS))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.completedScore").value(DEFAULT_COMPLETED_SCORE.booleanValue()))
            .andExpect(jsonPath("$.publicJury").value(DEFAULT_PUBLIC_JURY.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCompetition() throws Exception {
        // Get the competition
        restCompetitionMockMvc.perform(get("/api/competitions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompetition() throws Exception {
        // Initialize the database
        competitionRepository.saveAndFlush(competition);

		int databaseSizeBeforeUpdate = competitionRepository.findAll().size();

        // Update the competition
        competition.setName(UPDATED_NAME);
        competition.setDescription(UPDATED_DESCRIPTION);
        competition.setDeadline(UPDATED_DEADLINE);
        competition.setRules(UPDATED_RULES);
        competition.setInscriptionTime(UPDATED_INSCRIPTION_TIME);
        competition.setMaxNRecipesByChefs(UPDATED_MAX_NRECIPES_BY_CHEFS);
        competition.setCreationDate(UPDATED_CREATION_DATE);
        competition.setCompletedScore(UPDATED_COMPLETED_SCORE);
        competition.setPublicJury(UPDATED_PUBLIC_JURY);
        

        restCompetitionMockMvc.perform(put("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isOk());

        // Validate the Competition in the database
        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeUpdate);
        Competition testCompetition = competitions.get(competitions.size() - 1);
        assertThat(testCompetition.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompetition.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testCompetition.getDeadline()).isEqualTo(UPDATED_DEADLINE);
        assertThat(testCompetition.getRules()).isEqualTo(UPDATED_RULES);
        assertThat(testCompetition.getInscriptionTime()).isEqualTo(UPDATED_INSCRIPTION_TIME);
        assertThat(testCompetition.getMaxNRecipesByChefs()).isEqualTo(UPDATED_MAX_NRECIPES_BY_CHEFS);
        assertThat(testCompetition.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testCompetition.getCompletedScore()).isEqualTo(UPDATED_COMPLETED_SCORE);
        assertThat(testCompetition.getPublicJury()).isEqualTo(UPDATED_PUBLIC_JURY);
    }

    @Test
    @Transactional
    public void deleteCompetition() throws Exception {
        // Initialize the database
        competitionRepository.saveAndFlush(competition);

		int databaseSizeBeforeDelete = competitionRepository.findAll().size();

        // Get the competition
        restCompetitionMockMvc.perform(delete("/api/competitions/{id}", competition.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
