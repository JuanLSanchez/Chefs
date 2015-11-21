package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.TestConstants;
import es.juanlsanchez.chefs.domain.SocialPicture;
import es.juanlsanchez.chefs.repository.SocialPictureRepository;

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
 * Test class for the SocialPictureResource REST controller.
 *
 * @see SocialPictureResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SocialPictureResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";
    private static final String DEFAULT_URL = "SAMPLE_TEXT";
    private static final String UPDATED_URL = "UPDATED_TEXT";
    private static final String DEFAULT_PROPERTIES = "SAMPLE_TEXT";
    private static final String UPDATED_PROPERTIES = "UPDATED_TEXT";

    @Inject
    private SocialPictureRepository socialPictureRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSocialPictureMockMvc;

    private SocialPicture socialPicture;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SocialPictureResource socialPictureResource = new SocialPictureResource();
        ReflectionTestUtils.setField(socialPictureResource, "socialPictureRepository", socialPictureRepository);
        this.restSocialPictureMockMvc = MockMvcBuilders.standaloneSetup(socialPictureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        socialPicture = new SocialPicture();
        socialPicture.setTitle(DEFAULT_TITLE);
        socialPicture.setUrl(DEFAULT_URL);
        socialPicture.setProperties(DEFAULT_PROPERTIES);
    }

    @Test
    @Transactional
    public void createSocialPicture() throws Exception {
        int databaseSizeBeforeCreate = socialPictureRepository.findAll().size();

        // Create the SocialPicture

        restSocialPictureMockMvc.perform(post("/api/socialPictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(socialPicture)))
                .andExpect(status().isCreated());

        // Validate the SocialPicture in the database
        List<SocialPicture> socialPictures = socialPictureRepository.findAll();
        assertThat(socialPictures).hasSize(databaseSizeBeforeCreate + 1);
        SocialPicture testSocialPicture = socialPictures.get(socialPictures.size() - 1);
        assertThat(testSocialPicture.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSocialPicture.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testSocialPicture.getProperties()).isEqualTo(DEFAULT_PROPERTIES);
    }

    @Test
    @Transactional
    public void getAllSocialPictures() throws Exception {
        // Initialize the database
        socialPictureRepository.saveAndFlush(socialPicture);

        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the socialPictures
        restSocialPictureMockMvc.perform(get("/api/socialPictures"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(socialPicture.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
                .andExpect(jsonPath("$.[*].properties").value(hasItem(DEFAULT_PROPERTIES.toString())));
    }

    @Test
    @Transactional
    public void getSocialPicture() throws Exception {
        // Initialize the database
        socialPictureRepository.saveAndFlush(socialPicture);

        // Get the socialPicture
        restSocialPictureMockMvc.perform(get("/api/socialPictures/{id}", socialPicture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(socialPicture.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.properties").value(DEFAULT_PROPERTIES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSocialPicture() throws Exception {
        // Get the socialPicture
        restSocialPictureMockMvc.perform(get("/api/socialPictures/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSocialPicture() throws Exception {
        // Initialize the database
        socialPictureRepository.saveAndFlush(socialPicture);

		int databaseSizeBeforeUpdate = socialPictureRepository.findAll().size();

        // Update the socialPicture
        socialPicture.setTitle(UPDATED_TITLE);
        socialPicture.setUrl(UPDATED_URL);
        socialPicture.setProperties(UPDATED_PROPERTIES);


        restSocialPictureMockMvc.perform(put("/api/socialPictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(socialPicture)))
                .andExpect(status().isOk());

        // Validate the SocialPicture in the database
        List<SocialPicture> socialPictures = socialPictureRepository.findAll();
        assertThat(socialPictures).hasSize(databaseSizeBeforeUpdate);
        SocialPicture testSocialPicture = socialPictures.get(socialPictures.size() - 1);
        assertThat(testSocialPicture.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSocialPicture.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testSocialPicture.getProperties()).isEqualTo(UPDATED_PROPERTIES);
    }

    @Test
    @Transactional
    public void deleteSocialPicture() throws Exception {
        // Initialize the database
        socialPictureRepository.saveAndFlush(socialPicture);

		int databaseSizeBeforeDelete = socialPictureRepository.findAll().size();

        // Get the socialPicture
        restSocialPictureMockMvc.perform(delete("/api/socialPictures/{id}", socialPicture.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<SocialPicture> socialPictures = socialPictureRepository.findAll();
        assertThat(socialPictures).hasSize(databaseSizeBeforeDelete - 1);
    }
}
