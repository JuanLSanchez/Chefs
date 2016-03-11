package es.juanlsanchez.chefs.web.rest;

import com.google.common.collect.Lists;
import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.RecipeRepository;
import es.juanlsanchez.chefs.repository.UserRepository;
import es.juanlsanchez.chefs.service.RecipeService;
import es.juanlsanchez.chefs.service.UserService;
import es.juanlsanchez.chefs.web.rest.dto.SearchDTO;
import org.hamcrest.Matchers;
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
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SearchResource REST controller.
 *
 * @see SearchResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SearchResourceTest {

    private static final String DEFAULT_USER_PASSWORD = "user";

    @Inject
    private UserService userService;

    @Inject
    private RecipeService recipeService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private RecipeRepository recipeRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private ApplicationContext context;

    private MockMvc restSearchMockMvc;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SearchResource searchResource = new SearchResource();
        ReflectionTestUtils.setField(searchResource, "userService", userService);
        ReflectionTestUtils.setField(searchResource, "recipeService", recipeService);
        this.restSearchMockMvc = MockMvcBuilders.standaloneSetup(searchResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Test
    @Transactional
    public void searchAllLoginUsers() throws Exception {

        for(User user:userRepository.findAll().stream().filter(u -> u.getId()>3).collect(Collectors.toList())){
            String firstField, secondField, login, type;

            firstField = user.getLogin();
            secondField = user.getFirstName();
            login = user.getLogin();
            type = SearchDTO.TYPE_USER;

            ResultActions result = restSearchMockMvc.perform(get("/api/search/users/{q}", login));
            result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstField").value(firstField))
                .andExpect(jsonPath("$[0].secondField").value(secondField))
                .andExpect(jsonPath("$[0].login").value(login))
                .andExpect(jsonPath("$[0].type").value(type));
        }
    }

    @Test
    @Transactional
    public void searchByKey() throws Exception {
        List<String> searchs;

        searchs = Lists.newArrayList("u", "user", "00", "01", "user002");

        for(String q:searchs){
            List<User> users;
            List<String> firstField, secondField, login, type;

            users = userRepository.findAll().stream()
                .filter(u -> u.getId() > 3)
                .filter(u -> u.getFirstName().contains(q) || u.getLogin().contains(q))
                .collect(Collectors.toList());

            firstField = users.stream().map(User::getLogin).collect(Collectors.toList());
            secondField = users.stream().map(User::getFirstName).collect(Collectors.toList());
            login = users.stream().map(User::getLogin).collect(Collectors.toList());
            type = users.stream().map(u -> SearchDTO.TYPE_USER).collect(Collectors.toList());

            ResultActions result = restSearchMockMvc.perform(get("/api/search/users/{q}", q));
            result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].firstField", Matchers.containsInAnyOrder(firstField.toArray())))
                .andExpect(jsonPath("$[*].secondField", Matchers.containsInAnyOrder(secondField.toArray())))
                .andExpect(jsonPath("$[*].login", Matchers.containsInAnyOrder(login.toArray())))
                .andExpect(jsonPath("$[*].type", Matchers.containsInAnyOrder(type.toArray())));
        }
    }

    @Test
    @Transactional
    public void searchAllUsers() throws Exception {

        List<User> users;
        List<String> firstField, secondField, login, type;

        users = userRepository.findAll().stream()
            .filter(u -> u.getId() > 3)
            .collect(Collectors.toList());

        firstField = users.stream().map(User::getLogin).collect(Collectors.toList());
        secondField = users.stream().map(User::getFirstName).collect(Collectors.toList());
        login = users.stream().map(User::getLogin).collect(Collectors.toList());
        type = users.stream().map(u -> SearchDTO.TYPE_USER).collect(Collectors.toList());

        ResultActions result = restSearchMockMvc.perform(get("/api/search/users"));
        result
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[*].firstField", Matchers.containsInAnyOrder(firstField.toArray())))
            .andExpect(jsonPath("$[*].secondField", Matchers.containsInAnyOrder(secondField.toArray())))
            .andExpect(jsonPath("$[*].login", Matchers.containsInAnyOrder(login.toArray())))
            .andExpect(jsonPath("$[*].type", Matchers.containsInAnyOrder(type.toArray())));
    }

    @Test
    @Transactional
    public void searchAllRecipesPublics() throws Exception {
        for(Recipe recipe:recipeRepository.findAll().stream()
            .filter(r -> !r.getSocialEntity().getBlocked() && r.getSocialEntity().getIsPublic())
            .collect(Collectors.toList())){

            String firstField, secondField, login, type;

            firstField = recipe.getId().toString();
            secondField = recipe.getName();
            login = recipe.getUser().getLogin();
            type = SearchDTO.TYPE_RECIPE;

            ResultActions result = restSearchMockMvc.perform(get("/api/search/recipes/{q}", secondField));
            result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].firstField").value(firstField))
                .andExpect(jsonPath("$[0].secondField").value(secondField))
                .andExpect(jsonPath("$[0].login").value(login))
                .andExpect(jsonPath("$[0].type").value(type));
        }

    }

    @Test
    @Transactional
    public void searchRecipesWithUsersByQuery() throws Exception {

        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        List<String> searchs = Lists.newArrayList("r", "recipe", "recipe00", "01", "02", "03", "recipe001", "asdfasdf");
        for(String q:searchs){
            for(User user:userRepository.findAll().stream().filter(u->u.getId()>3).collect(Collectors.toList())){
                List<String> firstField, secondField, login, type;
                List<Recipe> recipes;
                String principalLogin;

                principalLogin = user.getLogin();

                recipes = recipeRepository.findAll().stream()
                    .filter(r -> r.getName().contains(q))
                    .filter(r -> (!r.getSocialEntity().getBlocked() && r.getSocialEntity().getIsPublic())       // Is public
                        || r.getUser().getLogin().equals(principalLogin)                                         // Principal as owner
                        || (!r.getSocialEntity().getBlocked() && r.getUser().getAcceptRequests().stream()        // Is follower
                        .anyMatch(request -> request.getAccepted()              // Request accepted
                            && request.getFollower().getLogin().equals(principalLogin))))  // Request of principal
                    .collect(Collectors.toList());
                firstField = recipes.stream().map(r -> r.getId().toString()).collect(Collectors.toList());
                secondField = recipes.stream().map(Recipe::getName).collect(Collectors.toList());
                login = recipes.stream().map(r -> r.getUser().getLogin()).collect(Collectors.toList());
                type = recipes.stream().map(r -> SearchDTO.TYPE_RECIPE).collect(Collectors.toList());

                SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(principalLogin, DEFAULT_USER_PASSWORD)));

                ResultActions result = restSearchMockMvc.perform(get("/api/search/recipes/{q}", q));
                result
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[*].firstField", Matchers.containsInAnyOrder(firstField.toArray())))
                    .andExpect(jsonPath("$[*].secondField", Matchers.containsInAnyOrder(secondField.toArray())))
                    .andExpect(jsonPath("$[*].login", Matchers.containsInAnyOrder(login.toArray())))
                    .andExpect(jsonPath("$[*].type", Matchers.containsInAnyOrder(type.toArray())));
            }
        }
    }

    @Test
    @Transactional
    public void searchRecipesWithoutUsersByQuery() throws Exception {
        List<String> searchs;

        searchs = Lists.newArrayList("r", "recipe", "recipe00", "01", "02", "03", "recipe001", "asdfasdf");

        for(String q:searchs){
            List<String> firstField, secondField, login, type;
            List<Recipe> recipes;

            recipes = recipeRepository.findAll().stream()
                .filter(r -> r.getName().contains(q))
                .filter(r -> !r.getSocialEntity().getBlocked() && r.getSocialEntity().getIsPublic())
                .collect(Collectors.toList());
            firstField = recipes.stream().map(r -> r.getId().toString()).collect(Collectors.toList());
            secondField = recipes.stream().map(Recipe::getName).collect(Collectors.toList());
            login = recipes.stream().map(r -> r.getUser().getLogin()).collect(Collectors.toList());
            type = recipes.stream().map(r -> SearchDTO.TYPE_RECIPE).collect(Collectors.toList());

            ResultActions result = restSearchMockMvc.perform(get("/api/search/recipes/{q}", q));
            result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].firstField", Matchers.containsInAnyOrder(firstField.toArray())))
                .andExpect(jsonPath("$[*].secondField", Matchers.containsInAnyOrder(secondField.toArray())))
                .andExpect(jsonPath("$[*].login", Matchers.containsInAnyOrder(login.toArray())))
                .andExpect(jsonPath("$[*].type", Matchers.containsInAnyOrder(type.toArray())));
        }
    }

}
