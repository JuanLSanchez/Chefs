package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.TestConstants;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;

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
 * Test class for the SocialEntityResource REST controller.
 *
 * @see SocialEntityResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SocialEntityResourceTest {


    private static final Integer DEFAULT_SUM_RATING = 1;
    private static final Integer UPDATED_SUM_RATING = 2;

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final Boolean DEFAULT_PUBLIC_INSCRIPTION = false;
    private static final Boolean UPDATED_PUBLIC_INSCRIPTION = true;

    private static final Boolean DEFAULT_BLOCKED = false;
    private static final Boolean UPDATED_BLOCKED = true;

    @Inject
    private SocialEntityRepository socialEntityRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSocialEntityMockMvc;

    private SocialEntity socialEntity;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SocialEntityResource socialEntityResource = new SocialEntityResource();
        ReflectionTestUtils.setField(socialEntityResource, "socialEntityRepository", socialEntityRepository);
        this.restSocialEntityMockMvc = MockMvcBuilders.standaloneSetup(socialEntityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        socialEntity = new SocialEntity();
        socialEntity.setSumRating(DEFAULT_SUM_RATING);
        socialEntity.setIsPublic(DEFAULT_IS_PUBLIC);
        socialEntity.setPublicInscription(DEFAULT_PUBLIC_INSCRIPTION);
        socialEntity.setBlocked(DEFAULT_BLOCKED);
    }

    @Test
    @Transactional
    public void createSocialEntity() throws Exception {
        int databaseSizeBeforeCreate = socialEntityRepository.findAll().size();

        // Create the SocialEntity

        restSocialEntityMockMvc.perform(post("/api/socialEntitys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(socialEntity)))
                .andExpect(status().isCreated());

        // Validate the SocialEntity in the database
        List<SocialEntity> socialEntitys = socialEntityRepository.findAll();
        assertThat(socialEntitys).hasSize(databaseSizeBeforeCreate + 1);
        SocialEntity testSocialEntity = socialEntitys.get(socialEntitys.size() - 1);
        assertThat(testSocialEntity.getSumRating()).isEqualTo(DEFAULT_SUM_RATING);
        assertThat(testSocialEntity.getIsPublic()).isEqualTo(DEFAULT_IS_PUBLIC);
        assertThat(testSocialEntity.getPublicInscription()).isEqualTo(DEFAULT_PUBLIC_INSCRIPTION);
        assertThat(testSocialEntity.getBlocked()).isEqualTo(DEFAULT_BLOCKED);
    }

    @Test
    @Transactional
    public void getAllSocialEntitys() throws Exception {
        // Initialize the database
        socialEntityRepository.saveAndFlush(socialEntity);

        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the socialEntitys
        restSocialEntityMockMvc.perform(get("/api/socialEntitys"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(socialEntity.getId().intValue())))
                .andExpect(jsonPath("$.[*].sumRating").value(hasItem(DEFAULT_SUM_RATING)))
                .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC.booleanValue())))
                .andExpect(jsonPath("$.[*].publicInscription").value(hasItem(DEFAULT_PUBLIC_INSCRIPTION.booleanValue())))
                .andExpect(jsonPath("$.[*].blocked").value(hasItem(DEFAULT_BLOCKED.booleanValue())));
    }

    @Test
    @Transactional
    public void getSocialEntity() throws Exception {
        // Initialize the database
        socialEntityRepository.saveAndFlush(socialEntity);

        // Get the socialEntity
        restSocialEntityMockMvc.perform(get("/api/socialEntitys/{id}", socialEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(socialEntity.getId().intValue()))
            .andExpect(jsonPath("$.sumRating").value(DEFAULT_SUM_RATING))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC.booleanValue()))
            .andExpect(jsonPath("$.publicInscription").value(DEFAULT_PUBLIC_INSCRIPTION.booleanValue()))
            .andExpect(jsonPath("$.blocked").value(DEFAULT_BLOCKED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSocialEntity() throws Exception {
        // Get the socialEntity
        restSocialEntityMockMvc.perform(get("/api/socialEntitys/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSocialEntity() throws Exception {
        // Initialize the database
        socialEntityRepository.saveAndFlush(socialEntity);

		int databaseSizeBeforeUpdate = socialEntityRepository.findAll().size();

        // Update the socialEntity
        socialEntity.setSumRating(UPDATED_SUM_RATING);
        socialEntity.setIsPublic(UPDATED_IS_PUBLIC);
        socialEntity.setPublicInscription(UPDATED_PUBLIC_INSCRIPTION);
        socialEntity.setBlocked(UPDATED_BLOCKED);


        restSocialEntityMockMvc.perform(put("/api/socialEntitys")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(socialEntity)))
                .andExpect(status().isOk());

        // Validate the SocialEntity in the database
        List<SocialEntity> socialEntitys = socialEntityRepository.findAll();
        assertThat(socialEntitys).hasSize(databaseSizeBeforeUpdate);
        SocialEntity testSocialEntity = socialEntitys.get(socialEntitys.size() - 1);
        assertThat(testSocialEntity.getSumRating()).isEqualTo(UPDATED_SUM_RATING);
        assertThat(testSocialEntity.getIsPublic()).isEqualTo(UPDATED_IS_PUBLIC);
        assertThat(testSocialEntity.getPublicInscription()).isEqualTo(UPDATED_PUBLIC_INSCRIPTION);
        assertThat(testSocialEntity.getBlocked()).isEqualTo(UPDATED_BLOCKED);
    }

    @Test
    @Transactional
    public void deleteSocialEntity() throws Exception {
        // Initialize the database
        socialEntityRepository.saveAndFlush(socialEntity);

		int databaseSizeBeforeDelete = socialEntityRepository.findAll().size();

        // Get the socialEntity
        restSocialEntityMockMvc.perform(delete("/api/socialEntitys/{id}", socialEntity.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<SocialEntity> socialEntitys = socialEntityRepository.findAll();
        assertThat(socialEntitys).hasSize(databaseSizeBeforeDelete - 1);
    }
}
