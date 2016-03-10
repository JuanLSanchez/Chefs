package es.juanlsanchez.chefs.web.rest;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Request;
import es.juanlsanchez.chefs.repository.RequestRepository;
import es.juanlsanchez.chefs.service.RequestService;
import org.assertj.core.api.StrictAssertions;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the RequestResource REST controller.
 *
 * @see RequestResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class RequestResourceTest {

    private static final String DEFAULT_LOGIN_USER = "user012";
    private static final String DEFAULT_USER_PASSWORD = "user";
    private static final String DEFAULT_FOLLOWED_USER = "user002";
    private static final String DEFAULT_FOLLOWER_USER = "user003";

    @Inject
    private RequestService requestService;

    @Inject
    private RequestRepository requestRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restRequestMockMvc;

    private Authentication defaultAuthentication, followedAuthentication, followerAuthentication;

    @Inject
    private ApplicationContext context;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RequestResource requestResource = new RequestResource();
        ReflectionTestUtils.setField(requestResource, "requestService", requestService);
        this.restRequestMockMvc = MockMvcBuilders.standaloneSetup(requestResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        this.defaultAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_LOGIN_USER, DEFAULT_USER_PASSWORD));
        this.followedAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_FOLLOWED_USER, DEFAULT_USER_PASSWORD));
        this.followerAuthentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(DEFAULT_FOLLOWER_USER, DEFAULT_USER_PASSWORD));
    }

    @Test
    @Transactional
    public void followAUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.defaultAuthentication);
        Integer requestInDBBefor;
        String followed;
        Request request;

        requestInDBBefor = requestRepository.findAll().size();
        followed = "user002";

        ResultActions result =restRequestMockMvc.perform(put("/api/requests/follower/{followed}", followed));
        result
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.followed.login").value(followed))
            .andExpect(jsonPath("$.followed.password", Matchers.isEmptyOrNullString()))
            .andExpect(jsonPath("$.follower.login").value(DEFAULT_LOGIN_USER))
            .andExpect(jsonPath("$.follower.password", Matchers.isEmptyOrNullString()));

        assertThat(requestRepository.findAll()).hasSize(requestInDBBefor + 1);

        request = requestRepository.findAll().get(requestInDBBefor);

        assertThat(request.getCreationDate()).isNotNull();
        assertThat(request.getCreationDate()).isLessThan(new DateTime());

        assertThat(request.getAccepted()).isFalse();
        assertThat(request.getIgnored()).isFalse();
        assertThat(request.getLocked()).isFalse();

        assertThat(request.getFollowed().getLogin()).isEqualTo(followed);
        assertThat(request.getFollower().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);
    }

    @Test
    @Transactional
    public void followAUserYouBlockYour() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.followerAuthentication);
        Integer requestInDBBefor;
        String followed;

        requestInDBBefor = requestRepository.findAll().size();
        followed = "user002";

        ResultActions result =restRequestMockMvc.perform(put("/api/requests/follower/{followed}", followed));
        result
            .andExpect(status().isBadRequest());

        assertThat(requestRepository.findAll()).hasSize(requestInDBBefor);
    }

    @Test
    @Transactional
    public void followAUserYouIgnoreYour() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.followerAuthentication);
        Integer requestInDBBefor;
        String followed, follower;

        requestInDBBefor = requestRepository.findAll().size();
        followed = "user004";
        follower = DEFAULT_FOLLOWER_USER;

        ResultActions result =restRequestMockMvc.perform(put("/api/requests/follower/{followed}", followed));
        result
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.followed.login").value(followed))
            .andExpect(jsonPath("$.followed.password", Matchers.isEmptyOrNullString()))
            .andExpect(jsonPath("$.follower.login").value(follower))
            .andExpect(jsonPath("$.follower.password", Matchers.isEmptyOrNullString()));

        assertThat(requestRepository.findAll()).hasSize(requestInDBBefor);
    }

    @Test
    @Transactional
    public void unfollowAUser() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(this.followerAuthentication);
        Integer requestInDBAfter;
        String followed;

        requestInDBAfter = requestRepository.findAll().size()-1;
        followed = "user005";

        ResultActions result =restRequestMockMvc.perform(put("/api/requests/follower/{followed}", followed));
        result
            .andExpect(status().isCreated());

        assertThat(requestRepository.findAll()).hasSize(requestInDBAfter);
    }

    @Test
    @Transactional
    public void acceptFollower() throws Exception {
        Integer requestInDBAfter;
        String follower,followed;
        String requestBody;
        Request request;

        follower = DEFAULT_LOGIN_USER;
        followed = DEFAULT_FOLLOWED_USER;
        requestBody =  RequestService.ACCEPTED_STATUS;

        SecurityContextHolder.getContext().setAuthentication(this.defaultAuthentication);
        requestService.update(followed);

        SecurityContextHolder.getContext().setAuthentication(this.followedAuthentication);

        requestInDBAfter = requestRepository.findAll().size();

        ResultActions result = restRequestMockMvc.perform(put("/api/requests/followed/{follower}", follower)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestBody)));
        result
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.followed.login").value(followed))
            .andExpect(jsonPath("$.followed.password", Matchers.isEmptyOrNullString()))
            .andExpect(jsonPath("$.follower.login").value(follower))
            .andExpect(jsonPath("$.follower.password", Matchers.isEmptyOrNullString()));

        assertThat(requestRepository.findAll()).hasSize(requestInDBAfter);

        request = requestRepository.findAll().get(requestRepository.findAll().size()-1);

        assertThat(request.getCreationDate()).isNotNull();
        assertThat(request.getCreationDate()).isLessThan(new DateTime());

        StrictAssertions.assertThat(request.getAccepted()).isTrue();
        StrictAssertions.assertThat(request.getIgnored()).isFalse();
        StrictAssertions.assertThat(request.getLocked()).isFalse();

        StrictAssertions.assertThat(request.getFollowed().getLogin()).isEqualTo(followed);
        StrictAssertions.assertThat(request.getFollower().getLogin()).isEqualTo(DEFAULT_LOGIN_USER);

    }

    @Test
    @Transactional
    public void blockFollower() throws Exception {
        Integer requestInDBAfter;
        String follower,followed;
        String requestBody;
        Request request;

        follower = DEFAULT_LOGIN_USER;
        followed = DEFAULT_FOLLOWED_USER;
        requestBody =  RequestService.LOCKED_STATUS;

        SecurityContextHolder.getContext().setAuthentication(this.defaultAuthentication);
        requestService.update(followed);

        SecurityContextHolder.getContext().setAuthentication(this.followedAuthentication);

        requestInDBAfter = requestRepository.findAll().size();

        ResultActions result = restRequestMockMvc.perform(put("/api/requests/followed/{follower}", follower)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestBody)));
        result
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.followed.login").value(followed))
            .andExpect(jsonPath("$.followed.password", Matchers.isEmptyOrNullString()))
            .andExpect(jsonPath("$.follower.login").value(follower))
            .andExpect(jsonPath("$.follower.password", Matchers.isEmptyOrNullString()));

        requestRepository.flush();
        assertThat(requestRepository.findAll()).hasSize(requestInDBAfter);

        request = requestRepository.findAll().stream()
            .filter(r -> r.getFollowed().getLogin().equals(followed) && r.getFollower().getLogin().equals(follower))
            .findFirst().get();

        assertThat(request.getCreationDate()).isNotNull();
        assertThat(request.getCreationDate()).isLessThan(new DateTime());

        StrictAssertions.assertThat(request.getAccepted()).isFalse();
        StrictAssertions.assertThat(request.getIgnored()).isFalse();
        StrictAssertions.assertThat(request.getLocked()).isTrue();

        StrictAssertions.assertThat(request.getFollowed().getLogin()).isEqualTo(followed);
        StrictAssertions.assertThat(request.getFollower().getLogin()).isEqualTo(follower);

    }

    @Test
    @Transactional
    public void ignoreFollower() throws Exception {
        Integer requestInDBAfter;
        String follower,followed;
        String requestBody;
        Request request;

        follower = DEFAULT_LOGIN_USER;
        followed = DEFAULT_FOLLOWED_USER;
        requestBody =  RequestService.IGNORED_STATUS;

        SecurityContextHolder.getContext().setAuthentication(this.defaultAuthentication);
        requestService.update(followed);

        SecurityContextHolder.getContext().setAuthentication(this.followedAuthentication);

        requestInDBAfter = requestRepository.findAll().size();

        ResultActions result = restRequestMockMvc.perform(put("/api/requests/followed/{follower}", follower)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestBody)));
        result
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.followed.login").value(followed))
            .andExpect(jsonPath("$.followed.password", Matchers.isEmptyOrNullString()))
            .andExpect(jsonPath("$.follower.login").value(follower))
            .andExpect(jsonPath("$.follower.password", Matchers.isEmptyOrNullString()));

        requestRepository.flush();
        assertThat(requestRepository.findAll()).hasSize(requestInDBAfter);

        request = requestRepository.findAll().stream()
            .filter(r -> r.getFollowed().getLogin().equals(followed) && r.getFollower().getLogin().equals(follower))
            .findFirst().get();

        assertThat(request.getCreationDate()).isNotNull();
        assertThat(request.getCreationDate()).isLessThan(new DateTime());

        StrictAssertions.assertThat(request.getAccepted()).isFalse();
        StrictAssertions.assertThat(request.getIgnored()).isTrue();
        StrictAssertions.assertThat(request.getLocked()).isFalse();

        StrictAssertions.assertThat(request.getFollowed().getLogin()).isEqualTo(followed);
        StrictAssertions.assertThat(request.getFollower().getLogin()).isEqualTo(follower);

    }

    @Test
    @Transactional
    public void acceptBlockFollower() throws Exception {
        Integer requestInDBAfter;
        Long requestId;
        final String followed;
        String requestBody, follower;
        Request request;

        followed = DEFAULT_FOLLOWED_USER;
        request = requestRepository.findAll().stream()
            .filter(r -> !r.getAccepted() && !r.getIgnored() && r.getLocked()
                && r.getFollowed().getLogin().equals(followed))
            .findFirst().get();
        follower = request.getFollower().getLogin();
        requestId = request.getId();

        requestBody = RequestService.ACCEPTED_STATUS;

        SecurityContextHolder.getContext().setAuthentication(this.followedAuthentication);

        requestInDBAfter = requestRepository.findAll().size();

        ResultActions result = restRequestMockMvc.perform(put("/api/requests/followed/{follower}", follower)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestBody)));
        result
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.followed.login").value(followed))
            .andExpect(jsonPath("$.followed.password", Matchers.isEmptyOrNullString()))
            .andExpect(jsonPath("$.follower.login").value(follower))
            .andExpect(jsonPath("$.follower.password", Matchers.isEmptyOrNullString()));

        assertThat(requestRepository.findAll()).hasSize(requestInDBAfter);

        request = requestRepository.getOne(requestId);

        assertThat(request.getCreationDate()).isNotNull();
        assertThat(request.getCreationDate()).isLessThan(new DateTime());

        StrictAssertions.assertThat(request.getAccepted()).isTrue();
        StrictAssertions.assertThat(request.getIgnored()).isFalse();
        StrictAssertions.assertThat(request.getLocked()).isFalse();

        StrictAssertions.assertThat(request.getFollowed().getLogin()).isEqualTo(followed);
        StrictAssertions.assertThat(request.getFollower().getLogin()).isEqualTo(follower);
    }

    @Test
    @Transactional
    public void blockAcceptFollower() throws Exception {
        Integer requestInDBAfter;
        Long requestId;
        final String followed;
        String requestBody, follower;
        Request request;

        followed = DEFAULT_FOLLOWED_USER;
        request = requestRepository.findAll().stream()
            .filter(r -> r.getAccepted() && !r.getIgnored() && !r.getLocked()
                && r.getFollowed().getLogin().equals(followed))
            .findFirst().get();
        follower = request.getFollower().getLogin();
        requestId = request.getId();

        requestBody = RequestService.LOCKED_STATUS;

        SecurityContextHolder.getContext().setAuthentication(this.followedAuthentication);

        requestInDBAfter = requestRepository.findAll().size();

        ResultActions result = restRequestMockMvc.perform(put("/api/requests/followed/{follower}", follower)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestBody)));
        result
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.followed.login").value(followed))
            .andExpect(jsonPath("$.followed.password", Matchers.isEmptyOrNullString()))
            .andExpect(jsonPath("$.follower.login").value(follower))
            .andExpect(jsonPath("$.follower.password", Matchers.isEmptyOrNullString()));

        assertThat(requestRepository.findAll()).hasSize(requestInDBAfter);

        request = requestRepository.getOne(requestId);

        assertThat(request.getCreationDate()).isNotNull();
        assertThat(request.getCreationDate()).isLessThan(new DateTime());

        StrictAssertions.assertThat(request.getAccepted()).isFalse();
        StrictAssertions.assertThat(request.getIgnored()).isFalse();
        StrictAssertions.assertThat(request.getLocked()).isTrue();

        StrictAssertions.assertThat(request.getFollowed().getLogin()).isEqualTo(followed);
        StrictAssertions.assertThat(request.getFollower().getLogin()).isEqualTo(follower);
    }

    @Test
    @Transactional
    public void ignoreAcceptFollower() throws Exception {
        Integer requestInDBAfter;
        Long requestId;
        final String followed;
        String requestBody, follower;
        Request request;

        followed = DEFAULT_FOLLOWED_USER;
        request = requestRepository.findAll().stream()
            .filter(r -> r.getAccepted() && !r.getIgnored() && !r.getLocked()
                && r.getFollowed().getLogin().equals(followed))
            .findFirst().get();
        follower = request.getFollower().getLogin();
        requestId = request.getId();

        requestBody = RequestService.IGNORED_STATUS;

        SecurityContextHolder.getContext().setAuthentication(this.followedAuthentication);

        requestInDBAfter = requestRepository.findAll().size();

        ResultActions result = restRequestMockMvc.perform(put("/api/requests/followed/{follower}", follower)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestBody)));
        result
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.followed.login").value(followed))
            .andExpect(jsonPath("$.followed.password", Matchers.isEmptyOrNullString()))
            .andExpect(jsonPath("$.follower.login").value(follower))
            .andExpect(jsonPath("$.follower.password", Matchers.isEmptyOrNullString()));

        assertThat(requestRepository.findAll()).hasSize(requestInDBAfter);

        request = requestRepository.getOne(requestId);

        assertThat(request.getCreationDate()).isNotNull();
        assertThat(request.getCreationDate()).isLessThan(new DateTime());

        StrictAssertions.assertThat(request.getAccepted()).isFalse();
        StrictAssertions.assertThat(request.getIgnored()).isTrue();
        StrictAssertions.assertThat(request.getLocked()).isFalse();

        StrictAssertions.assertThat(request.getFollowed().getLogin()).isEqualTo(followed);
        StrictAssertions.assertThat(request.getFollower().getLogin()).isEqualTo(follower);
    }
}
