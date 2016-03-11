package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.service.RecipeService;
import es.juanlsanchez.chefs.service.UserService;
import es.juanlsanchez.chefs.web.rest.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/search")
public class SearchResource {

    @Inject
    private UserService userService;

    @Inject
    private RecipeService recipeService;

    /**
     * GET  /users/:q -> get all users filter by first name and login.
     */
    @RequestMapping(value = "/users/{q}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<SearchDTO>> findAllLikeLoginOrLikeFirstName(@PathVariable String q, Pageable pageable)
        throws URISyntaxException {
        Page<User> users = userService.findAllLikeLoginOrLikeFirstName(q, pageable);
        List<SearchDTO> searchDTO = users.getContent().stream()
            .map(SearchDTO::new)
            .collect(Collectors.toList());
        return new ResponseEntity<>(searchDTO, HttpStatus.OK);
    }

    /**
     * GET  /users -> get all users.
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<SearchDTO>> getAllUsersByLoginAndFirstName(Pageable pageable)
        throws URISyntaxException {
        Page<User> users = userService.findAllUsers(pageable);
        List<SearchDTO> searchDTO = users.getContent().stream()
            .map(SearchDTO::new)
            .collect(Collectors.toList());
        return new ResponseEntity<>(searchDTO, HttpStatus.OK);
    }

    /**
     * GET  /recipes/:q -> get all recipes filter by name.
     */
    @RequestMapping(value = "/recipes/{q}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<SearchDTO>> getAllRecipesIsVisibilityAndLikeName(@PathVariable String q, Pageable pageable)
        throws URISyntaxException {
        Page<Recipe> recipes = recipeService.findAllIsVisibilityAndLikeName(q, pageable);
        List<SearchDTO> searchDTO = recipes.getContent().stream()
            .map(SearchDTO::new)
            .collect(Collectors.toList());
        return new ResponseEntity<>(searchDTO, HttpStatus.OK);
    }


}
