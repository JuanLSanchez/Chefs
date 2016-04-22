package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.service.RecipeService;
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
import org.springframework.transaction.annotation.Transactional;
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
        ResponseEntity<Recipe> result;
        Recipe recipeResult;
        if (recipe.getId() != null) {
            result = ResponseEntity
                        .badRequest()
                        .header("Failure", "A new recipe cannot already have an ID")
                        .body(null);
        }else{
            try{
                recipeResult = recipeService.save(recipe);
                log.debug("REST response recipe save : {}", recipe);
                result = ResponseEntity
                    .created(new URI("/api/recipes/" + recipeResult.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("recipe", recipeResult.getId().toString()))
                    .body(recipeResult);
            }catch (IllegalArgumentException e){
                log.debug("IllegalArgumentException: {}", e.getMessage());
                result = ResponseEntity
                            .badRequest()
                            .header("Failure", "A recipe cannot already have an social entity")
                            .body(null);
            }catch (Exception e){
                log.debug("Exception: {}", e);
                result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return result;
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

        return Optional.ofNullable(recipeService.save(recipe))
            .map(r -> ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("recipe", r.getId().toString()))
                .body(r))
            .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
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
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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
            "/api/recipes/findAllIsVisibilityAndLikeName/"+name);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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
        Page<RecipeMiniDTO> page = recipeService.findDTOAllByLoginAndIsVisibility(login, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recipes_dto/user/"+login);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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
        Page<RecipeMiniDTO> page = recipeService.findDTOByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/recipes_dto/user");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
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
        Page<RecipeMiniDTO> page = recipeService.findDTOAllIsVisibilityAndLikeName(name, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page,
            "/api/recipes_dto/findAllIsVisibilityAndLikeName/"+name);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /recipes_dto/withTag/{tag} -> get all recipes filter by name.
     */
    @RequestMapping(value = "/recipes_dto/withTag/{tag}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<RecipeMiniDTO>> getAllRecipesIsVisibilityByTag(@PathVariable Long tag, Pageable pageable)
        throws URISyntaxException {
        Page<RecipeMiniDTO> recipes;
        HttpHeaders headers;
        ResponseEntity<List<RecipeMiniDTO>> result;

        try{
            recipes = recipeService.findAllIsVisibilityAndSocialEntityTagId(tag, pageable);
            headers = PaginationUtil.generatePaginationHttpHeaders(recipes, "/api/recipes_dto/withTag/" + tag);
            result = new ResponseEntity<>(recipes.getContent(), headers, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * GET  /recipes_dto/likes -> get all recipes that like to the user
     */
    @RequestMapping(value = "/recipes_dto/likes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<RecipeMiniDTO>> getAllRecipesLike(Pageable pageable)
        throws URISyntaxException {
        Page<RecipeMiniDTO> recipes;
        HttpHeaders headers;
        ResponseEntity<List<RecipeMiniDTO>> result;

        try{
            recipes = recipeService.findAllLikes(pageable);
            headers = PaginationUtil.generatePaginationHttpHeaders(recipes, "/api/recipes_dto/likes");
            result = new ResponseEntity<>(recipes.getContent(), headers, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * GET  /recipes_dto/assessed -> get all recipes that the user assess
     */
    @RequestMapping(value = "/recipes_dto/assessed",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<RecipeMiniDTO>> getAllRecipesAssessed(Pageable pageable)
        throws URISyntaxException {
        Page<RecipeMiniDTO> recipes;
        HttpHeaders headers;
        ResponseEntity<List<RecipeMiniDTO>> result;

        try{
            recipes = recipeService.findAllAssessed(pageable);
            headers = PaginationUtil.generatePaginationHttpHeaders(recipes, "/api/recipes_dto/likes");
            result = new ResponseEntity<>(recipes.getContent(), headers, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }


}
