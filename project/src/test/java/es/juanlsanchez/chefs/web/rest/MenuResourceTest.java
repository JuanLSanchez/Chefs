package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.TestConstants;
import es.juanlsanchez.chefs.domain.Menu;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Schedule;
import es.juanlsanchez.chefs.repository.MenuRepository;
import es.juanlsanchez.chefs.service.MenuService;
import es.juanlsanchez.chefs.service.RecipeService;
import es.juanlsanchez.chefs.service.ScheduleService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the MenuResource REST controller.
 *
 * @see MenuResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class MenuResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    private static final DateTime DEFAULT_TIME = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_TIME = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_TIME_STR = dateTimeFormatter.print(DEFAULT_TIME);
    private static final String UPDATED_TIME_STR = dateTimeFormatter.print(UPDATED_TIME);

    private static final String DEFAULT_LOGIN_USER = "user008";
    private static final String SECOND_LOGIN_USER = "user007";
    private static final String DEFAULT_USER_PASSWORD = "user";

    @Inject
    private MenuService menuService;

    @Inject
    private MenuRepository menuRepository;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private RecipeService recipeService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restMenuMockMvc;

    private Menu menu;

    private Authentication authentication, secondAuthentication;

    @Inject
    private ApplicationContext context;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MenuResource menuResource = new MenuResource();
        ReflectionTestUtils.setField(menuResource, "menuService", menuService);
        this.restMenuMockMvc = MockMvcBuilders.standaloneSetup(menuResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        menu = new Menu();
        menu.setTime(DEFAULT_TIME);

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
        this.secondAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(SECOND_LOGIN_USER, DEFAULT_USER_PASSWORD));
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    @Transactional
    public void createMenu() throws Exception {
        int databaseSizeBeforeCreate = menuRepository.findAll().size();
        Long scheduleId;
        ResultActions result;
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();

        // Create the Menu
        result = restMenuMockMvc.perform(post("/api/menus/{scheduleId}", scheduleId)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(menu)));
        result
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME_STR))
            .andExpect(jsonPath("$.schedule.id").value(scheduleId.intValue()));

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeCreate + 1);
        Menu testMenu = menus.get(menus.size() - 1);
        assertThat(testMenu.getTime().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_TIME);
        assertThat(testMenu.getSchedule().getId()).isEqualTo(scheduleId);
    }

    @Test
    @Transactional
    public void createMenuWithRequestOfOtherUser() throws Exception {
        int databaseSizeBeforeCreate = menuRepository.findAll().size();
        Long scheduleId;
        ResultActions result;
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        // Create the Menu
        result = restMenuMockMvc.perform(post("/api/menus/{scheduleId}", scheduleId)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(menu)));
        result
            .andExpect(status().isBadRequest());

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = menuRepository.findAll().size();
        Long scheduleId;
        ResultActions result;
        // set the field null
        menu.setTime(null);
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();

        // Create the Menu, which fails.

        result =restMenuMockMvc.perform(post("/api/menus/{scheduleId}", scheduleId)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menu)));
        result
            .andExpect(status().isBadRequest());

        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllMenus() throws Exception {
        // Initialize the database
        Long scheduleId;
        ResultActions result;
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();

        menuService.create(menu, scheduleId);
        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the menus
        result = restMenuMockMvc.perform(get("/api/menus/{scheduleId}", scheduleId));
        result
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(menu.getId().intValue())))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME_STR)));
    }

    @Test
    @Transactional
    public void getAllMenusWithNoExistingSchedule() throws Exception {
        // Initialize the database
        Long scheduleId;
        ResultActions result;
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        scheduleId = Long.MAX_VALUE;
        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the menus
        result = restMenuMockMvc.perform(get("/api/menus/{scheduleId}", scheduleId));
        result
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void getAllMenusWithRequestOfOtherUser() throws Exception {
        // Initialize the database
        Long scheduleId;
        ResultActions result;
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();

        menuService.create(menu, scheduleId);
        SecurityContextHolder.getContext().setAuthentication(this.authentication);
        // Set pageable
        pageableArgumentResolver.setFallbackPageable(new PageRequest(0, TestConstants.MAX_PAGE_SIZE));

        // Get all the menus
        result = restMenuMockMvc.perform(get("/api/menus/{scheduleId}", scheduleId));
        result
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void updateMenu() throws Exception {
        // Initialize the database
        Long scheduleId;
        ResultActions result;
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();

        menuService.create(menu, scheduleId);

		int databaseSizeBeforeUpdate = menuRepository.findAll().size();

        // Update the menu
        menu.setTime(UPDATED_TIME);

        result = restMenuMockMvc.perform(put("/api/menus/{scheduleId}", scheduleId)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(menu)));
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value(UPDATED_TIME_STR))
                .andExpect(jsonPath("$.schedule.id").value(scheduleId.intValue()));

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeUpdate);
        Menu testMenu = menus.get(menus.size() - 1);
        assertThat(testMenu.getTime().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_TIME);
    }

    @Test
    @Transactional
    public void deleteMenu() throws Exception {
        // Initialize the database
        Long scheduleId;
        ResultActions result;
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();

        menuService.create(menu, scheduleId);

        int databaseSizeBeforeDelete = menuRepository.findAll().size();

        // Get the menu
        result = restMenuMockMvc.perform(delete("/api/menus/{id}", menu.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8));
        result
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void deleteMenuOfOtherUser() throws Exception {
        // Initialize the database
        Long scheduleId;
        ResultActions result;
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();

        menuService.create(menu, scheduleId);
        SecurityContextHolder.getContext().setAuthentication(this.secondAuthentication);

        int databaseSizeBeforeDelete = menuRepository.findAll().size();

        // Get the menu
        result = restMenuMockMvc.perform(delete("/api/menus/{id}", menu.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8));
        result
            .andExpect(status().isBadRequest());

        // Validate the database is empty
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void addRecipeToMenu() throws  Exception {
        // Initialize the database
        Long scheduleId;
        ResultActions result;
        Recipe recipe;
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        scheduleId = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0).getId();
        recipe = recipeService.findAllIsVisibilityAndLikeName("r", new PageRequest(0, 1)).getContent().get(0);

        menuService.create(menu, scheduleId);

        int databaseSizeBeforeAdd = menuRepository.findAll().size();
        int databaseSizeMenuInRecipeBeforeAdd = recipe.getMenus().size();
        int databaseSizeRecipeInMenuBeforeAdd = menu.getRecipes().size();

        // Update the menu

        result = restMenuMockMvc.perform(put("/api/menus/addRecipe/{menuId}/{recipeId}", menu.getId(), recipe.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8));
        result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME_STR))
            .andExpect(jsonPath("$.schedule.id").value(scheduleId.intValue()))
            .andExpect(jsonPath("$.recipes." + recipe.getId()).exists());

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeAdd);
        Menu testMenu = menus.get(menus.size() - 1);
        assertThat(testMenu.getTime().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_TIME);
        assertThat(recipeService.findOne(recipe.getId()).getMenus()).hasSize(databaseSizeMenuInRecipeBeforeAdd + 1);
        assertThat(menuRepository.findOne(menu.getId()).getRecipes()).hasSize(databaseSizeRecipeInMenuBeforeAdd+1);

    }

    @Test
    @Transactional
    public void removeRecipeToMenu() throws  Exception {
        // Initialize the database
        Long scheduleId;
        Schedule schedule;
        ResultActions result;
        Recipe recipe;
        SecurityContextHolder.getContext().setAuthentication(this.authentication);

        schedule = scheduleService.findAllByUserIsCurrentUser(new PageRequest(0, 1)).getContent().get(0);
        scheduleId = schedule.getId();
        menu = schedule.getMenus().stream().findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No ha fallado la prueba si no el test, ya que no se han encontrado ningún menú"));
        recipe = menu.getRecipes().stream().findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No ha fallado la prueba si no el test, ya que no se han encontrado ninguna receta"));

        menuService.create(menu, scheduleId);

        int databaseSizeBeforeAdd = menuRepository.findAll().size();
        int databaseSizeRecipeInMenuBeforeAdd = menu.getRecipes().size();

        // Update the menu

        result = restMenuMockMvc.perform(put("/api/menus/removeRecipe/{menuId}/{recipeId}", menu.getId(), recipe.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8));
        result
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schedule.id").value(scheduleId.intValue()))
            .andExpect(jsonPath("$.recipes."+recipe.getId()).doesNotExist());

        // Validate the Menu in the database
        List<Menu> menus = menuRepository.findAll();
        assertThat(menus).hasSize(databaseSizeBeforeAdd);
        assertThat(menuRepository.findOne(menu.getId()).getRecipes()).hasSize(databaseSizeRecipeInMenuBeforeAdd-1);

    }
}
