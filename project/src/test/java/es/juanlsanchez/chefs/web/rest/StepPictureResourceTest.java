package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.StepPicture;
import es.juanlsanchez.chefs.repository.StepPictureRepository;

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
 * Test class for the StepPictureResource REST controller.
 *
 * @see StepPictureResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class StepPictureResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";

    private static final byte[] DEFAULT_SRC = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_SRC = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_PROPERTIES = "SAMPLE_TEXT";
    private static final String UPDATED_PROPERTIES = "UPDATED_TEXT";

    @Inject
    private StepPictureRepository stepPictureRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restStepPictureMockMvc;

    private StepPicture stepPicture;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StepPictureResource stepPictureResource = new StepPictureResource();
        ReflectionTestUtils.setField(stepPictureResource, "stepPictureRepository", stepPictureRepository);
        this.restStepPictureMockMvc = MockMvcBuilders.standaloneSetup(stepPictureResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        stepPicture = new StepPicture();
        stepPicture.setTitle(DEFAULT_TITLE);
        stepPicture.setSrc(DEFAULT_SRC);
        stepPicture.setProperties(DEFAULT_PROPERTIES);
    }

    @Test
    @Transactional
    public void createStepPicture() throws Exception {
        int databaseSizeBeforeCreate = stepPictureRepository.findAll().size();

        // Create the StepPicture

        restStepPictureMockMvc.perform(post("/api/stepPictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stepPicture)))
                .andExpect(status().isCreated());

        // Validate the StepPicture in the database
        List<StepPicture> stepPictures = stepPictureRepository.findAll();
        assertThat(stepPictures).hasSize(databaseSizeBeforeCreate + 1);
        StepPicture testStepPicture = stepPictures.get(stepPictures.size() - 1);
        assertThat(testStepPicture.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testStepPicture.getSrc()).isEqualTo(DEFAULT_SRC);
        assertThat(testStepPicture.getProperties()).isEqualTo(DEFAULT_PROPERTIES);
    }

    @Test
    @Transactional
    public void getAllStepPictures() throws Exception {
        // Initialize the database
        stepPictureRepository.saveAndFlush(stepPicture);

        // Get all the stepPictures
        restStepPictureMockMvc.perform(get("/api/stepPictures"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(stepPicture.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].src").value(hasItem(Base64Utils.encodeToString(DEFAULT_SRC))))
                .andExpect(jsonPath("$.[*].properties").value(hasItem(DEFAULT_PROPERTIES.toString())));
    }

    @Test
    @Transactional
    public void getStepPicture() throws Exception {
        // Initialize the database
        stepPictureRepository.saveAndFlush(stepPicture);

        // Get the stepPicture
        restStepPictureMockMvc.perform(get("/api/stepPictures/{id}", stepPicture.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(stepPicture.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.src").value(Base64Utils.encodeToString(DEFAULT_SRC)))
            .andExpect(jsonPath("$.properties").value(DEFAULT_PROPERTIES.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStepPicture() throws Exception {
        // Get the stepPicture
        restStepPictureMockMvc.perform(get("/api/stepPictures/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStepPicture() throws Exception {
        // Initialize the database
        stepPictureRepository.saveAndFlush(stepPicture);

		int databaseSizeBeforeUpdate = stepPictureRepository.findAll().size();

        // Update the stepPicture
        stepPicture.setTitle(UPDATED_TITLE);
        stepPicture.setSrc(UPDATED_SRC);
        stepPicture.setProperties(UPDATED_PROPERTIES);
        

        restStepPictureMockMvc.perform(put("/api/stepPictures")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stepPicture)))
                .andExpect(status().isOk());

        // Validate the StepPicture in the database
        List<StepPicture> stepPictures = stepPictureRepository.findAll();
        assertThat(stepPictures).hasSize(databaseSizeBeforeUpdate);
        StepPicture testStepPicture = stepPictures.get(stepPictures.size() - 1);
        assertThat(testStepPicture.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testStepPicture.getSrc()).isEqualTo(UPDATED_SRC);
        assertThat(testStepPicture.getProperties()).isEqualTo(UPDATED_PROPERTIES);
    }

    @Test
    @Transactional
    public void deleteStepPicture() throws Exception {
        // Initialize the database
        stepPictureRepository.saveAndFlush(stepPicture);

		int databaseSizeBeforeDelete = stepPictureRepository.findAll().size();

        // Get the stepPicture
        restStepPictureMockMvc.perform(delete("/api/stepPictures/{id}", stepPicture.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<StepPicture> stepPictures = stepPictureRepository.findAll();
        assertThat(stepPictures).hasSize(databaseSizeBeforeDelete - 1);
    }
}
