package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Vote;
import es.juanlsanchez.chefs.repository.VoteRepository;

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
 * Test class for the VoteResource REST controller.
 *
 * @see VoteResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class VoteResourceTest {


    private static final Boolean DEFAULT_IS_FINAL = false;
    private static final Boolean UPDATED_IS_FINAL = true;

    private static final Boolean DEFAULT_ABSTAIN = false;
    private static final Boolean UPDATED_ABSTAIN = true;
    private static final String DEFAULT_COMMENT = "SAMPLE_TEXT";
    private static final String UPDATED_COMMENT = "UPDATED_TEXT";

    private static final Boolean DEFAULT_COMPLETED_SCORE = false;
    private static final Boolean UPDATED_COMPLETED_SCORE = true;

    @Inject
    private VoteRepository voteRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restVoteMockMvc;

    private Vote vote;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        VoteResource voteResource = new VoteResource();
        ReflectionTestUtils.setField(voteResource, "voteRepository", voteRepository);
        this.restVoteMockMvc = MockMvcBuilders.standaloneSetup(voteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        vote = new Vote();
        vote.setIsFinal(DEFAULT_IS_FINAL);
        vote.setAbstain(DEFAULT_ABSTAIN);
        vote.setComment(DEFAULT_COMMENT);
        vote.setCompletedScore(DEFAULT_COMPLETED_SCORE);
    }

    @Test
    @Transactional
    public void createVote() throws Exception {
        int databaseSizeBeforeCreate = voteRepository.findAll().size();

        // Create the Vote

        restVoteMockMvc.perform(post("/api/votes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(vote)))
                .andExpect(status().isCreated());

        // Validate the Vote in the database
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(databaseSizeBeforeCreate + 1);
        Vote testVote = votes.get(votes.size() - 1);
        assertThat(testVote.getIsFinal()).isEqualTo(DEFAULT_IS_FINAL);
        assertThat(testVote.getAbstain()).isEqualTo(DEFAULT_ABSTAIN);
        assertThat(testVote.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testVote.getCompletedScore()).isEqualTo(DEFAULT_COMPLETED_SCORE);
    }

    @Test
    @Transactional
    public void getAllVotes() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the votes
        restVoteMockMvc.perform(get("/api/votes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())))
                .andExpect(jsonPath("$.[*].isFinal").value(hasItem(DEFAULT_IS_FINAL.booleanValue())))
                .andExpect(jsonPath("$.[*].abstain").value(hasItem(DEFAULT_ABSTAIN.booleanValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].completedScore").value(hasItem(DEFAULT_COMPLETED_SCORE.booleanValue())));
    }

    @Test
    @Transactional
    public void getVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get the vote
        restVoteMockMvc.perform(get("/api/votes/{id}", vote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(vote.getId().intValue()))
            .andExpect(jsonPath("$.isFinal").value(DEFAULT_IS_FINAL.booleanValue()))
            .andExpect(jsonPath("$.abstain").value(DEFAULT_ABSTAIN.booleanValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.completedScore").value(DEFAULT_COMPLETED_SCORE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingVote() throws Exception {
        // Get the vote
        restVoteMockMvc.perform(get("/api/votes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

		int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Update the vote
        vote.setIsFinal(UPDATED_IS_FINAL);
        vote.setAbstain(UPDATED_ABSTAIN);
        vote.setComment(UPDATED_COMMENT);
        vote.setCompletedScore(UPDATED_COMPLETED_SCORE);
        

        restVoteMockMvc.perform(put("/api/votes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(vote)))
                .andExpect(status().isOk());

        // Validate the Vote in the database
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = votes.get(votes.size() - 1);
        assertThat(testVote.getIsFinal()).isEqualTo(UPDATED_IS_FINAL);
        assertThat(testVote.getAbstain()).isEqualTo(UPDATED_ABSTAIN);
        assertThat(testVote.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testVote.getCompletedScore()).isEqualTo(UPDATED_COMPLETED_SCORE);
    }

    @Test
    @Transactional
    public void deleteVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

		int databaseSizeBeforeDelete = voteRepository.findAll().size();

        // Get the vote
        restVoteMockMvc.perform(delete("/api/votes/{id}", vote.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
