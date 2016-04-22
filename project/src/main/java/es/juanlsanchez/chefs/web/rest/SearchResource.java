package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Tag;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.service.RecipeService;
import es.juanlsanchez.chefs.service.TagService;
import es.juanlsanchez.chefs.service.UserService;
import es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO;
import es.juanlsanchez.chefs.web.rest.dto.SearchDTO;
import es.juanlsanchez.chefs.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
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

    @Inject
    private TagService tagService;

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
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(users, "/api/search/users/" + q);
        return new ResponseEntity<>(searchDTO, headers, HttpStatus.OK);
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
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(users, "/api/search/users");
        return new ResponseEntity<>(searchDTO, headers, HttpStatus.OK);
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
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(recipes, "/api/search/recipes/" + q);
        return new ResponseEntity<>(searchDTO, headers, HttpStatus.OK);
    }

    /**
     * GET  /tags/:q -> get all recipes filter by name.
     */
    @RequestMapping(value = "/tags/{q}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<SearchDTO>> getAllTagsLikeName(@PathVariable String q, Pageable pageable)
        throws URISyntaxException {
        Page<Tag> tags = tagService.findAllByNameContains(q, pageable);
        List<SearchDTO> searchDTO = tags.getContent().stream()
            .map(SearchDTO::new)
            .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(tags, "/api/search/tags/" + q);
        return new ResponseEntity<>(searchDTO, headers, HttpStatus.OK);
    }


}
