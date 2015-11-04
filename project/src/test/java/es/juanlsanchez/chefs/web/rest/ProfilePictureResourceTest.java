package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.ProfilePicture;
import es.juanlsanchez.chefs.repository.ProfilePictureRepository;

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
 * Test class for the ProfilePictureResource REST controller.
 *
 * @see ProfilePictureResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ProfilePictureResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";
    private static final String DEFAULT_URL = "SAMPLE_TEXT";
    private static final String UPDATED_URL = "UPDATED_TEXT";
    private static final String DEFAULT_PROPERTIES = "SAMPLE_TEXT";
    private static final String UPDATED_PROPERTIES = "UPDATED_TEXT";

    @Inject
    private ProfilePictureRepository profilePictureRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restProfilePictureMockMvc;

    private ProfilePicture profilePicture;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProfilePictureResource profilePictureResource = new ProfilePictureResource();
        ReflectionTestUtils.setField(profilePictureResource, "profilePictureRepository", profilePictureRepository);
        this.restProfilePictureMockMvc = MockMvcBuilders.standaloneSetup(profilePictureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        profilePicture = new ProfilePicture();
        profilePicture.setTitle(DEFAULT_TITLE);
        profilePicture.setUrl(DEFAULT_URL);
        profilePicture.setProperties(DEFAULT_PROPERTIES);
    }

    @Test
    @Transactional
    public void createProfilePicture() throws Exception {
        int databaseSizeBeforeCreate = profilePictureRepository.findAll().size();

        // Create the ProfilePicture

        restProfilePictureMockMvc.perform(post("/api/profilePictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(profilePicture)))
                .andExpect(status().isCreated());

        // Validate the ProfilePicture in the database
        List<ProfilePicture> profilePictures = profilePictureRepository.findAll();
        assertThat(profilePictures).hasSize(databaseSizeBeforeCreate + 1);
        ProfilePicture testProfilePicture = profilePictures.get(profilePictures.size() - 1);
        assertThat(testProfilePicture.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testProfilePicture.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testProfilePicture.getProperties()).isEqualTo(DEFAULT_PROPERTIES);
    }

    @Test
    @Transactional
    public void getAllProfilePictures() throws Exception {
        // Initialize the database
        profilePictureRepository.saveAndFlush(profilePicture);

        // Get all the profilePictures
        restProfilePictureMockMvc.perform(get("/api/profilePictures"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(profilePicture.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
                .andExpect(jsonPath("$.[*].properties").value(hasItem(DEFAULT_PROPERTIES.toString())));
    }

    @Test
    @Transactional
    public void getProfilePicture() throws Exception {
        // Initialize the database
        profilePictureRepository.saveAndFlush(profilePicture);

        // Get the profilePicture
        restProfilePictureMockMvc.perform(get("/api/profilePictures/{id}", profilePicture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(profilePicture.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.properties").value(DEFAULT_PROPERTIES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProfilePicture() throws Exception {
        // Get the profilePicture
        restProfilePictureMockMvc.perform(get("/api/profilePictures/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProfilePicture() throws Exception {
        // Initialize the database
        profilePictureRepository.saveAndFlush(profilePicture);

		int databaseSizeBeforeUpdate = profilePictureRepository.findAll().size();

        // Update the profilePicture
        profilePicture.setTitle(UPDATED_TITLE);
        profilePicture.setUrl(UPDATED_URL);
        profilePicture.setProperties(UPDATED_PROPERTIES);
        

        restProfilePictureMockMvc.perform(put("/api/profilePictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(profilePicture)))
                .andExpect(status().isOk());

        // Validate the ProfilePicture in the database
        List<ProfilePicture> profilePictures = profilePictureRepository.findAll();
        assertThat(profilePictures).hasSize(databaseSizeBeforeUpdate);
        ProfilePicture testProfilePicture = profilePictures.get(profilePictures.size() - 1);
        assertThat(testProfilePicture.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testProfilePicture.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testProfilePicture.getProperties()).isEqualTo(UPDATED_PROPERTIES);
    }

    @Test
    @Transactional
    public void deleteProfilePicture() throws Exception {
        // Initialize the database
        profilePictureRepository.saveAndFlush(profilePicture);

		int databaseSizeBeforeDelete = profilePictureRepository.findAll().size();

        // Get the profilePicture
        restProfilePictureMockMvc.perform(delete("/api/profilePictures/{id}", profilePicture.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ProfilePicture> profilePictures = profilePictureRepository.findAll();
        assertThat(profilePictures).hasSize(databaseSizeBeforeDelete - 1);
    }
}
