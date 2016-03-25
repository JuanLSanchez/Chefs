package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.TestConstants;
import es.juanlsanchez.chefs.domain.Schedule;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.MenuRepository;
import es.juanlsanchez.chefs.repository.ScheduleRepository;
import es.juanlsanchez.chefs.repository.UserRepository;
import es.juanlsanchez.chefs.service.ScheduleService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private static final String DEFAULT_LOGIN_USER = "user008";
    private static final String DEFAULT_USER_PASSWORD = "user";

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private MenuRepository menuRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restScheduleMockMvc;

    private Schedule schedule;

    private Authentication authentication;

    @Inject
    private ApplicationContext context;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ScheduleResource scheduleResource = new ScheduleResource();
        ReflectionTestUtils.setField(scheduleResource, "scheduleService", scheduleService);
        this.restScheduleMockMvc = MockMvcBuilders.standaloneSetup(scheduleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        schedule = new Schedule();
        schedule.setName(DEFAULT_NAME);
        schedule.setDescription(DEFAULT_DESCRIPTION);

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    @Transactional
    public void createSchedule() throws Exception {
        int databaseSizeBeforeCreate = scheduleService.findAll().size();

        // Create the Schedule
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        restScheduleMockMvc.perform(post("/api/schedules")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(schedule.getName()))
                .andExpect(jsonPath("$.description").value(schedule.getDescription()))
                .andExpect(jsonPath("$.user.login").value(DEFAULT_LOGIN_USER));

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeCreate + 1);
        Schedule testSchedule = schedules.get(schedules.size() - 1);
        assertThat(testSchedule.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSchedule.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleService.findAll().size();
        // set the field null
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        schedule.setName(null);

        // Create the Schedule, which fails.

        restScheduleMockMvc.perform(post("/api/schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedule)))
                .andExpect(status().isBadRequest());

        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleService.findAll().size();
        // set the field null
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        schedule.setDescription(null);

        // Create the Schedule, which fails.

        restScheduleMockMvc.perform(post("/api/schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedule)))
            .andExpect(status().isBadRequest());

        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSchedules() throws Exception {

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);

        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        for(User user:userRepository.findAll().stream().filter(u->u.getId()>3).collect(Collectors.toList())){
            List<Schedule> schedules;
            String principalLogin;
            List<String> names, logins, descriptions;

            principalLogin = user.getLogin();
            schedules = scheduleRepository.findAll().stream()
                .filter(s -> s.getUser().getLogin().equals(principalLogin))
                .collect(Collectors.toList());
            names = schedules.stream().map(Schedule::getName).collect(Collectors.toList());
            descriptions = schedules.stream().map(Schedule::getDescription).collect(Collectors.toList());
            logins = schedules.stream().map(s -> s.getUser().getLogin()).collect(Collectors.toList());

            SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(principalLogin, DEFAULT_USER_PASSWORD)));

            ResultActions result = restScheduleMockMvc.perform(get("/api/schedules"));
            result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder(names.toArray())))
                .andExpect(jsonPath("$[*].description", Matchers.containsInAnyOrder(descriptions.toArray())))
                .andExpect(jsonPath("$[*].user.login", Matchers.containsInAnyOrder(logins.toArray())));
        }

    }

    @Test
    @Transactional
    public void getAllSchedulesWithoutPrincipal() throws Exception {

        ResultActions result = restScheduleMockMvc.perform(get("/api/schedules"));
        result
            .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    public void getSchedule() throws Exception {

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);

        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        for(User user:userRepository.findAll().stream().filter(u->u.getId()>3).collect(Collectors.toList())){
            Optional<Schedule> schedule;
            String principalLogin;

            principalLogin = user.getLogin();
            schedule = scheduleRepository.findAll().stream()
                .filter(s -> s.getUser().getLogin().equals(principalLogin))
                .findFirst();

            if (schedule.isPresent()){

                SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(principalLogin, DEFAULT_USER_PASSWORD)));

                // Get the schedule
                restScheduleMockMvc.perform(get("/api/schedules/{id}", schedule.get().getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(schedule.get().getId().intValue()))
                    .andExpect(jsonPath("$.name").value(schedule.get().getName()))
                    .andExpect(jsonPath("$.description").value(schedule.get().getDescription()));
            }
        }
    }

    @Test
    @Transactional
    public void getScheduleOfOtherUser() throws Exception {

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);

        Optional<Schedule> schedule;
        String principalLogin;

        principalLogin = DEFAULT_LOGIN_USER;
        schedule = scheduleRepository.findAll().stream()
            .filter(s -> !s.getUser().getLogin().equals(principalLogin))
            .findFirst();

        SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(principalLogin, DEFAULT_USER_PASSWORD)));

        // Get the schedule
        restScheduleMockMvc.perform(get("/api/schedules/{id}", schedule.get().getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getScheduleWithoutUser() throws Exception {

        // Get the schedule
        restScheduleMockMvc.perform(get("/api/schedules/{id}", 0))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getNonExistingSchedule() throws Exception {
        // Get the schedule
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        restScheduleMockMvc.perform(get("/api/schedules/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSchedule() throws Exception {
        // Initialize the database
        Integer databaseSizeBeforeUpdate =scheduleRepository.findAll().size();
        schedule = scheduleRepository.findAll().stream()
            .filter(s -> s.getUser().getLogin().equals(DEFAULT_LOGIN_USER))
            .findFirst().get();

        // Update the schedule
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        schedule.setName(UPDATED_NAME);
        schedule.setDescription(UPDATED_DESCRIPTION);

        restScheduleMockMvc.perform(put("/api/schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedule)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(schedule.getId().intValue()))
            .andExpect(jsonPath("$.name").value(schedule.getName()))
            .andExpect(jsonPath("$.description").value(schedule.getDescription()));

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void updateScheduleOfOtherUser() throws Exception {
        // Initialize the database
        Integer databaseSizeBeforeUpdate =scheduleRepository.findAll().size();
        schedule = scheduleRepository.findAll().stream()
            .filter(s -> !s.getUser().getLogin().equals(DEFAULT_LOGIN_USER))
            .findFirst().get();

        // Update the schedule
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        schedule.setName(UPDATED_NAME);
        schedule.setDescription(UPDATED_DESCRIPTION);

        restScheduleMockMvc.perform(put("/api/schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedule)))
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void updateScheduleWithoutUser() throws Exception {
        // Initialize the database
        Integer databaseSizeBeforeUpdate =scheduleRepository.findAll().size();
        schedule = scheduleRepository.findAll().stream()
            .filter(s -> !s.getUser().getLogin().equals(DEFAULT_LOGIN_USER))
            .findFirst().get();

        // Update the schedule
        schedule.setName(UPDATED_NAME);
        schedule.setDescription(UPDATED_DESCRIPTION);

        restScheduleMockMvc.perform(put("/api/schedules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(schedule)))
            .andExpect(status().isBadRequest());

        // Validate the Schedule in the database
        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSchedule() throws Exception {
        // Initialize the database
        int databaseSizeBeforeDelete =scheduleRepository.findAll().size();
        int databaseSizeOfMenuBeforeDelete = menuRepository.findAll().size();
        schedule = scheduleRepository.findAll().stream()
            .filter(s -> s.getUser().getLogin().equals(DEFAULT_LOGIN_USER))
            .findFirst().get();
        int numbersOfMenu = schedule.getMenus().size();

        // Get the schedule
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        restScheduleMockMvc.perform(delete("/api/schedules/{id}", schedule.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeDelete - 1);
        assertThat(menuRepository.findAll()).hasSize(databaseSizeOfMenuBeforeDelete-numbersOfMenu);
    }

    @Test
    @Transactional
    public void deleteScheduleOfOtherUser() throws Exception {
        // Initialize the database
        int databaseSizeBeforeDelete =scheduleRepository.findAll().size();
        schedule = scheduleRepository.findAll().stream()
            .filter(s -> !s.getUser().getLogin().equals(DEFAULT_LOGIN_USER))
            .findFirst().get();

        // Get the schedule
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        restScheduleMockMvc.perform(delete("/api/schedules/{id}", schedule.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the database is empty
        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void deleteScheduleWithoutUser() throws Exception {
        // Initialize the database
        int databaseSizeBeforeDelete =scheduleRepository.findAll().size();

        // Get the schedule
        restScheduleMockMvc.perform(delete("/api/schedules/{id}", 0)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        // Validate the database is empty
        List<Schedule> schedules = scheduleService.findAll();
        assertThat(schedules).hasSize(databaseSizeBeforeDelete);
    }
}
