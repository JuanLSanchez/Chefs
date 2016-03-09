package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class UserResourceTest {

    private static final String ADMIN_LOGIN_USER = "admin";

    private static final String DEFAULT_LOGIN_USER = "user";
    private static final String USER002_LOGIN = "user002";
    private static final String DEFAULT_FIRST_NAME_USER = "User";
    private static final int DEFAULT_NUMBER_USERS = 17;
    private static final java.lang.Object DEFAULT_USER_PASSWORD = "user";

    @Inject
    private UserService userService;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restUserMockMvc;

    private Authentication authenticationAsUser002;

    @Inject
    private ApplicationContext context;

    @PostConstruct
    public void setup() {
        UserResource userResource = new UserResource();
        ReflectionTestUtils.setField(userResource, "userService", userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void init(){
        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.authenticationAsUser002 = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(USER002_LOGIN, DEFAULT_USER_PASSWORD));

    }

    @Test
    public void testGetExistingUserAndAdministrator() throws Exception {
        restUserMockMvc.perform(get("/api/users/{login}", ADMIN_LOGIN_USER)
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/{login}", DEFAULT_LOGIN_USER))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME_USER));
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown")
            .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllUsers() throws Exception{
        restUserMockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(DEFAULT_NUMBER_USERS)));
    }

    @Test
    public void findAllLikeLoginOrLikeFirstName() throws Exception{
        restUserMockMvc.perform(get("/api/users/likeLoginOrLikeFirstName/{q}", "user"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(DEFAULT_NUMBER_USERS)));
    }

    @Test
    public void findAllLikeLoginOrLikeFirstNameToAdmin() throws Exception{
        restUserMockMvc.perform(get("/api/users/likeLoginOrLikeFirstName/{q}", "admin"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.empty()));
    }

    @Test
    public void findAllFollowersByLogin() throws Exception{
        restUserMockMvc.perform(get("/api/users/followers/{followed}", USER002_LOGIN))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(3)));
    }

    @Test
    public void findAllFollowersByLoginAsTheUser() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(this.authenticationAsUser002);
        restUserMockMvc.perform(get("/api/users/followers/{followed}", USER002_LOGIN))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    public void findAllFollowingByLogin() throws Exception{
        restUserMockMvc.perform(get("/api/users/following/{follower}", "user003"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }
}
