package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.ActivityLog;
import es.juanlsanchez.chefs.repository.ActivityLogRepository;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ActivityLogResource REST controller.
 *
 * @see ActivityLogResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ActivityLogResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    private static final Integer DEFAULT_ID_OF_CUSTOMER = 1;
    private static final Integer UPDATED_ID_OF_CUSTOMER = 2;
    private static final String DEFAULT_NAME_OF_CUSTOMER = "SAMPLE_TEXT";
    private static final String UPDATED_NAME_OF_CUSTOMER = "UPDATED_TEXT";
    private static final String DEFAULT_PICTURE_URL = "SAMPLE_TEXT";
    private static final String UPDATED_PICTURE_URL = "UPDATED_TEXT";
    private static final String DEFAULT_OBJECT_TYPE = "SAMPLE_TEXT";
    private static final String UPDATED_OBJECT_TYPE = "UPDATED_TEXT";
    private static final String DEFAULT_VERB = "SAMPLE_TEXT";
    private static final String UPDATED_VERB = "UPDATED_TEXT";

    private static final DateTime DEFAULT_MOMENT = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_MOMENT = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_MOMENT_STR = dateTimeFormatter.print(DEFAULT_MOMENT);
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
    private static final String DEFAULT_TAGS = "SAMPLE_TEXT";
    private static final String UPDATED_TAGS = "UPDATED_TEXT";

    @Inject
    private ActivityLogRepository activityLogRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restActivityLogMockMvc;

    private ActivityLog activityLog;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ActivityLogResource activityLogResource = new ActivityLogResource();
        ReflectionTestUtils.setField(activityLogResource, "activityLogRepository", activityLogRepository);
        this.restActivityLogMockMvc = MockMvcBuilders.standaloneSetup(activityLogResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        activityLog = new ActivityLog();
        activityLog.setIdOfCustomer(DEFAULT_ID_OF_CUSTOMER);
        activityLog.setNameOfCustomer(DEFAULT_NAME_OF_CUSTOMER);
        activityLog.setPictureUrl(DEFAULT_PICTURE_URL);
        activityLog.setObjectType(DEFAULT_OBJECT_TYPE);
        activityLog.setVerb(DEFAULT_VERB);
        activityLog.setMoment(DEFAULT_MOMENT);
        activityLog.setName(DEFAULT_NAME);
        activityLog.setDescription(DEFAULT_DESCRIPTION);
        activityLog.setTags(DEFAULT_TAGS);
    }

    @Test
    @Transactional
    public void createActivityLog() throws Exception {
        int databaseSizeBeforeCreate = activityLogRepository.findAll().size();

        // Create the ActivityLog

        restActivityLogMockMvc.perform(post("/api/activityLogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activityLog)))
                .andExpect(status().isCreated());

        // Validate the ActivityLog in the database
        List<ActivityLog> activityLogs = activityLogRepository.findAll();
        assertThat(activityLogs).hasSize(databaseSizeBeforeCreate + 1);
        ActivityLog testActivityLog = activityLogs.get(activityLogs.size() - 1);
        assertThat(testActivityLog.getIdOfCustomer()).isEqualTo(DEFAULT_ID_OF_CUSTOMER);
        assertThat(testActivityLog.getNameOfCustomer()).isEqualTo(DEFAULT_NAME_OF_CUSTOMER);
        assertThat(testActivityLog.getPictureUrl()).isEqualTo(DEFAULT_PICTURE_URL);
        assertThat(testActivityLog.getObjectType()).isEqualTo(DEFAULT_OBJECT_TYPE);
        assertThat(testActivityLog.getVerb()).isEqualTo(DEFAULT_VERB);
        assertThat(testActivityLog.getMoment().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_MOMENT);
        assertThat(testActivityLog.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testActivityLog.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testActivityLog.getTags()).isEqualTo(DEFAULT_TAGS);
    }

    @Test
    @Transactional
    public void getAllActivityLogs() throws Exception {
        // Initialize the database
        activityLogRepository.saveAndFlush(activityLog);

        // Get all the activityLogs
        restActivityLogMockMvc.perform(get("/api/activityLogs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(activityLog.getId().intValue())))
                .andExpect(jsonPath("$.[*].idOfCustomer").value(hasItem(DEFAULT_ID_OF_CUSTOMER)))
                .andExpect(jsonPath("$.[*].nameOfCustomer").value(hasItem(DEFAULT_NAME_OF_CUSTOMER.toString())))
                .andExpect(jsonPath("$.[*].pictureUrl").value(hasItem(DEFAULT_PICTURE_URL.toString())))
                .andExpect(jsonPath("$.[*].objectType").value(hasItem(DEFAULT_OBJECT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].verb").value(hasItem(DEFAULT_VERB.toString())))
                .andExpect(jsonPath("$.[*].moment").value(hasItem(DEFAULT_MOMENT_STR)))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS.toString())));
    }

    @Test
    @Transactional
    public void getActivityLog() throws Exception {
        // Initialize the database
        activityLogRepository.saveAndFlush(activityLog);

        // Get the activityLog
        restActivityLogMockMvc.perform(get("/api/activityLogs/{id}", activityLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(activityLog.getId().intValue()))
            .andExpect(jsonPath("$.idOfCustomer").value(DEFAULT_ID_OF_CUSTOMER))
            .andExpect(jsonPath("$.nameOfCustomer").value(DEFAULT_NAME_OF_CUSTOMER.toString()))
            .andExpect(jsonPath("$.pictureUrl").value(DEFAULT_PICTURE_URL.toString()))
            .andExpect(jsonPath("$.objectType").value(DEFAULT_OBJECT_TYPE.toString()))
            .andExpect(jsonPath("$.verb").value(DEFAULT_VERB.toString()))
            .andExpect(jsonPath("$.moment").value(DEFAULT_MOMENT_STR))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingActivityLog() throws Exception {
        // Get the activityLog
        restActivityLogMockMvc.perform(get("/api/activityLogs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivityLog() throws Exception {
        // Initialize the database
        activityLogRepository.saveAndFlush(activityLog);

		int databaseSizeBeforeUpdate = activityLogRepository.findAll().size();

        // Update the activityLog
        activityLog.setIdOfCustomer(UPDATED_ID_OF_CUSTOMER);
        activityLog.setNameOfCustomer(UPDATED_NAME_OF_CUSTOMER);
        activityLog.setPictureUrl(UPDATED_PICTURE_URL);
        activityLog.setObjectType(UPDATED_OBJECT_TYPE);
        activityLog.setVerb(UPDATED_VERB);
        activityLog.setMoment(UPDATED_MOMENT);
        activityLog.setName(UPDATED_NAME);
        activityLog.setDescription(UPDATED_DESCRIPTION);
        activityLog.setTags(UPDATED_TAGS);
        

        restActivityLogMockMvc.perform(put("/api/activityLogs")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activityLog)))
                .andExpect(status().isOk());

        // Validate the ActivityLog in the database
        List<ActivityLog> activityLogs = activityLogRepository.findAll();
        assertThat(activityLogs).hasSize(databaseSizeBeforeUpdate);
        ActivityLog testActivityLog = activityLogs.get(activityLogs.size() - 1);
        assertThat(testActivityLog.getIdOfCustomer()).isEqualTo(UPDATED_ID_OF_CUSTOMER);
        assertThat(testActivityLog.getNameOfCustomer()).isEqualTo(UPDATED_NAME_OF_CUSTOMER);
        assertThat(testActivityLog.getPictureUrl()).isEqualTo(UPDATED_PICTURE_URL);
        assertThat(testActivityLog.getObjectType()).isEqualTo(UPDATED_OBJECT_TYPE);
        assertThat(testActivityLog.getVerb()).isEqualTo(UPDATED_VERB);
        assertThat(testActivityLog.getMoment().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_MOMENT);
        assertThat(testActivityLog.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testActivityLog.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testActivityLog.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    @Transactional
    public void deleteActivityLog() throws Exception {
        // Initialize the database
        activityLogRepository.saveAndFlush(activityLog);

		int databaseSizeBeforeDelete = activityLogRepository.findAll().size();

        // Get the activityLog
        restActivityLogMockMvc.perform(delete("/api/activityLogs/{id}", activityLog.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ActivityLog> activityLogs = activityLogRepository.findAll();
        assertThat(activityLogs).hasSize(databaseSizeBeforeDelete - 1);
    }
}
