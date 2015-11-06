package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Schedule;
import es.juanlsanchez.chefs.repository.ScheduleRepository;

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
 * Test class for the ScheduleResource REST controller.
 *
 * @see ScheduleResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ScheduleResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScheduleResource scheduleResource = new ScheduleResource();
        ReflectionTestUtils.setField(scheduleResource, "scheduleRepository", scheduleRepository);
        this.restScheduleMockMvc = MockMvcBuilders.standaloneSetup(scheduleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        schedule = new Schedule();
        schedule.setName(DEFAULT_NAME);
        schedule.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createSchedule() throws Exception {
        int databaseSizeBeforeCreate = scheduleRepository.findAll().size();

        // Create the Schedule

        restScheduleMockMvc.perform(post("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isCreated());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeCreate + 1);
        Schedule testSchedule = schedules.get(schedules.size() - 1);
        assertThat(testSchedule.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSchedule.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().size();
        // set the field null
        schedule.setName(null);

        // Create the Schedule, which fails.

        restScheduleMockMvc.perform(post("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isBadRequest());

        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().size();
        // set the field null
        schedule.setDescription(null);

        // Create the Schedule, which fails.

        restScheduleMockMvc.perform(post("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isBadRequest());

        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSchedules() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get all the schedules
        restScheduleMockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(schedule.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

        // Get the schedule
        restScheduleMockMvc.perform(get("/api/schedules/{id}", schedule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(schedule.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSchedule() throws Exception {
        // Get the schedule
        restScheduleMockMvc.perform(get("/api/schedules/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

		int databaseSizeBeforeUpdate = scheduleRepository.findAll().size();

        // Update the schedule
        schedule.setName(UPDATED_NAME);
        schedule.setDescription(UPDATED_DESCRIPTION);
        

        restScheduleMockMvc.perform(put("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isOk());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = schedules.get(schedules.size() - 1);
        assertThat(testSchedule.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSchedule.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.saveAndFlush(schedule);

		int databaseSizeBeforeDelete = scheduleRepository.findAll().size();

        // Get the schedule
        restScheduleMockMvc.perform(delete("/api/schedules/{id}", schedule.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Schedule> schedules = scheduleRepository.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeDelete - 1);
    }
}
