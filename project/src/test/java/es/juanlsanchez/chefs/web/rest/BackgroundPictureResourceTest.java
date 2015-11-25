package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.BackgroundPicture;
import es.juanlsanchez.chefs.repository.BackgroundPictureRepository;

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
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the BackgroundPictureResource REST controller.
 *
 * @see BackgroundPictureResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class BackgroundPictureResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";

    private static final byte[] DEFAULT_SRC = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_SRC = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_PROPERTIES = "SAMPLE_TEXT";
    private static final String UPDATED_PROPERTIES = "UPDATED_TEXT";

    @Inject
    private BackgroundPictureRepository backgroundPictureRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBackgroundPictureMockMvc;

    private BackgroundPicture backgroundPicture;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BackgroundPictureResource backgroundPictureResource = new BackgroundPictureResource();
        ReflectionTestUtils.setField(backgroundPictureResource, "backgroundPictureRepository", backgroundPictureRepository);
        this.restBackgroundPictureMockMvc = MockMvcBuilders.standaloneSetup(backgroundPictureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        backgroundPicture = new BackgroundPicture();
        backgroundPicture.setTitle(DEFAULT_TITLE);
        backgroundPicture.setSrc(DEFAULT_SRC);
        backgroundPicture.setProperties(DEFAULT_PROPERTIES);
    }

    @Test
    @Transactional
    public void createBackgroundPicture() throws Exception {
        int databaseSizeBeforeCreate = backgroundPictureRepository.findAll().size();

        // Create the BackgroundPicture

        restBackgroundPictureMockMvc.perform(post("/api/backgroundPictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(backgroundPicture)))
                .andExpect(status().isCreated());

        // Validate the BackgroundPicture in the database
        List<BackgroundPicture> backgroundPictures = backgroundPictureRepository.findAll();
        assertThat(backgroundPictures).hasSize(databaseSizeBeforeCreate + 1);
        BackgroundPicture testBackgroundPicture = backgroundPictures.get(backgroundPictures.size() - 1);
        assertThat(testBackgroundPicture.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBackgroundPicture.getSrc()).isEqualTo(DEFAULT_SRC);
        assertThat(testBackgroundPicture.getProperties()).isEqualTo(DEFAULT_PROPERTIES);
    }

    @Test
    @Transactional
    public void getAllBackgroundPictures() throws Exception {
        // Initialize the database
        backgroundPictureRepository.saveAndFlush(backgroundPicture);

        // Get all the backgroundPictures
        restBackgroundPictureMockMvc.perform(get("/api/backgroundPictures"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(backgroundPicture.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].src").value(hasItem(Base64Utils.encodeToString(DEFAULT_SRC))))
                .andExpect(jsonPath("$.[*].properties").value(hasItem(DEFAULT_PROPERTIES.toString())));
    }

    @Test
    @Transactional
    public void getBackgroundPicture() throws Exception {
        // Initialize the database
        backgroundPictureRepository.saveAndFlush(backgroundPicture);

        // Get the backgroundPicture
        restBackgroundPictureMockMvc.perform(get("/api/backgroundPictures/{id}", backgroundPicture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(backgroundPicture.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.src").value(Base64Utils.encodeToString(DEFAULT_SRC)))
            .andExpect(jsonPath("$.properties").value(DEFAULT_PROPERTIES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBackgroundPicture() throws Exception {
        // Get the backgroundPicture
        restBackgroundPictureMockMvc.perform(get("/api/backgroundPictures/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBackgroundPicture() throws Exception {
        // Initialize the database
        backgroundPictureRepository.saveAndFlush(backgroundPicture);

		int databaseSizeBeforeUpdate = backgroundPictureRepository.findAll().size();

        // Update the backgroundPicture
        backgroundPicture.setTitle(UPDATED_TITLE);
        backgroundPicture.setSrc(UPDATED_SRC);
        backgroundPicture.setProperties(UPDATED_PROPERTIES);
        

        restBackgroundPictureMockMvc.perform(put("/api/backgroundPictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(backgroundPicture)))
                .andExpect(status().isOk());

        // Validate the BackgroundPicture in the database
        List<BackgroundPicture> backgroundPictures = backgroundPictureRepository.findAll();
        assertThat(backgroundPictures).hasSize(databaseSizeBeforeUpdate);
        BackgroundPicture testBackgroundPicture = backgroundPictures.get(backgroundPictures.size() - 1);
        assertThat(testBackgroundPicture.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBackgroundPicture.getSrc()).isEqualTo(UPDATED_SRC);
        assertThat(testBackgroundPicture.getProperties()).isEqualTo(UPDATED_PROPERTIES);
    }

    @Test
    @Transactional
    public void deleteBackgroundPicture() throws Exception {
        // Initialize the database
        backgroundPictureRepository.saveAndFlush(backgroundPicture);

		int databaseSizeBeforeDelete = backgroundPictureRepository.findAll().size();

        // Get the backgroundPicture
        restBackgroundPictureMockMvc.perform(delete("/api/backgroundPictures/{id}", backgroundPicture.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<BackgroundPicture> backgroundPictures = backgroundPictureRepository.findAll();
        assertThat(backgroundPictures).hasSize(databaseSizeBeforeDelete - 1);
    }
}
