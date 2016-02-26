package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.service.RecipeService;
import es.juanlsanchez.chefs.service.UserService;
import es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO;
import es.juanlsanchez.chefs.web.rest.util.HeaderUtil;
import es.juanlsanchez.chefs.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing Recipe.
 */
@RestController
@RequestMapping("/api")
public class RecipeResource {

    private final Logger log = LoggerFactory.getLogger(RecipeResource.class);

    @Autowired
    private UserService userService;

    @Autowired RecipeService recipeService;

    /**
     * POST  /recipes -> Create a new recipe.
     */
    @RequestMapping(value = "/recipes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) throws URISyntaxException {
        log.debug("REST request to save Recipe : {}", recipe);
        if (recipe.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new recipe cannot already have an ID").body(null);
        }

        Recipe result;

        result = recipeService.save(recipe);
        System.out.println("Creado");

        return ResponseEntity.created(new URI("/api/recipes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("recipe", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /recipes -> Updates an existing recipe.
     */
    @RequestMapping(value = "/recipes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recipe> updateRecipe(@Valid @RequestBody Recipe recipe) throws URISyntaxException {
        log.debug("REST request to update Recipe : {}", recipe);
        if (recipe.getId() == null) {
            return createRecipe(recipe);
        }
        Recipe result = recipeService.save(recipe);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("recipe", recipe.getId().toString()))
            .body(result);
    }

    /**
     * GET  /recipes -> get all the recipes.
     */
    @RequestMapping(value = "/recipes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Recipe>> getAllRecipes(Pageable pageable)
        throws URISyntaxException {
        Page<Recipe> page = recipeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recipes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /recipes/:id -> get the "id" recipe.
     */
    @RequestMapping(value = "/recipes/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Recipe> getRecipe(@PathVariable Long id) {
        log.debug("REST request to get Recipe : {}", id);
        return Optional.ofNullable(recipeService.findOne(id))
            .map(recipe -> new ResponseEntity<>(
                recipe,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET /recipes/user/{login} -> get all the recipes of the user
     */
    @RequestMapping(value = "/recipes/user/{login}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Recipe>> findAllByLoginAndIsVisibility(@PathVariable String login, Pageable pageable)
        throws URISyntaxException {
        Page<Recipe> page = recipeService.findAllByLoginAndIsVisibility(login, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recipes/user/" + login);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET /recipes/user -> get all the recipes of the principal
     */
    @RequestMapping(value = "/recipes/user",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Recipe>> getAllRecipesPrincipalUser(Pageable pageable)
        throws URISyntaxException {
        Page<Recipe> page = recipeService.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recipes/user");
        ResponseEntity result = new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        return result;
    }

    /**
     * GET /recipes/findAllIsVisibilityAndLikeName/{name} -> get all the recipes like name
     */
    @RequestMapping(value = "/recipes/findAllIsVisibilityAndLikeName/{name}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Recipe>> findAllIsVisibilityAndLikeName(@PathVariable String name, Pageable pageable)
        throws URISyntaxException {
        Page<Recipe> page = recipeService.findAllIsVisibilityAndLikeName(name, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,
            "/recipes/findAllIsVisibilityAndLikeName/"+name);
        ResponseEntity result = new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        return result;
    }

    /**
     * GET /recipes_dto/user/{login} -> get all the recipes of the user
     */
    @RequestMapping(value = "/recipes_dto/user/{login}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RecipeMiniDTO>> findAllDTOByLoginAndIsVisibility(@PathVariable String login, Pageable pageable)
        throws URISyntaxException {
        Page<Recipe> page = recipeService.findAllByLoginAndIsVisibility(login, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recipes/user/"+login);
        return new ResponseEntity<>(
            page.getContent().stream().map(RecipeMiniDTO::new).collect(Collectors.toList()), headers, HttpStatus.OK);
    }

    /**
     * GET /recipes_dto/user -> get all the recipes of the principal
     */
    @RequestMapping(value = "/recipes_dto/user",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RecipeMiniDTO>> findAllDTORecipesPrincipalUser(Pageable pageable)
        throws URISyntaxException {
        Page<Recipe> page = recipeService.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recipes/user");
        ResponseEntity result = new ResponseEntity<>(
            page.getContent().stream().map(RecipeMiniDTO::new).collect(Collectors.toList()), headers, HttpStatus.OK);
        return result;
    }

    /**
     * GET /recipes_dto/findAllIsVisibilityAndLikeName/{name} -> get all the recipes like name
     */
    @RequestMapping(value = "/recipes_dto/findAllIsVisibilityAndLikeName/{name}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RecipeMiniDTO>> findAllDTOIsVisibilityAndLikeName(@PathVariable String name, Pageable pageable)
        throws URISyntaxException {
        Page<Recipe> page = recipeService.findAllIsVisibilityAndLikeName(name, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,
            "/recipes/findAllIsVisibilityAndLikeName/"+name);
        ResponseEntity result = new ResponseEntity<>(
            page.getContent().stream().map(RecipeMiniDTO::new).collect(Collectors.toList()), headers, HttpStatus.OK);
        return result;
    }
}
