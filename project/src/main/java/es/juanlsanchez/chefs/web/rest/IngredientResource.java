package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Ingredient;
import es.juanlsanchez.chefs.repository.IngredientRepository;
import es.juanlsanchez.chefs.web.rest.util.HeaderUtil;
import es.juanlsanchez.chefs.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Ingredient.
 */
@RestController
@RequestMapping("/api")
public class IngredientResource {

    private final Logger log = LoggerFactory.getLogger(IngredientResource.class);

    @Inject
    private IngredientRepository ingredientRepository;

    /**
     * POST  /ingredients -> Create a new ingredient.
     */
    @RequestMapping(value = "/ingredients",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ingredient> createIngredient(@Valid @RequestBody Ingredient ingredient) throws URISyntaxException {
        log.debug("REST request to save Ingredient : {}", ingredient);
        if (ingredient.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new ingredient cannot already have an ID").body(null);
        }
        Ingredient result = ingredientRepository.save(ingredient);
        return ResponseEntity.created(new URI("/api/ingredients/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("ingredient", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /ingredients -> Updates an existing ingredient.
     */
    @RequestMapping(value = "/ingredients",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ingredient> updateIngredient(@Valid @RequestBody Ingredient ingredient) throws URISyntaxException {
        log.debug("REST request to update Ingredient : {}", ingredient);
        if (ingredient.getId() == null) {
            return createIngredient(ingredient);
        }
        Ingredient result = ingredientRepository.save(ingredient);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("ingredient", ingredient.getId().toString()))
                .body(result);
    }

    /**
     * GET  /ingredients -> get all the ingredients.
     */
    @RequestMapping(value = "/ingredients",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Ingredient>> getAllIngredients(Pageable pageable)
        throws URISyntaxException {
        Page<Ingredient> page = ingredientRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ingredients");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /ingredients/:id -> get the "id" ingredient.
     */
    @RequestMapping(value = "/ingredients/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ingredient> getIngredient(@PathVariable Long id) {
        log.debug("REST request to get Ingredient : {}", id);
        return Optional.ofNullable(ingredientRepository.findOne(id))
            .map(ingredient -> new ResponseEntity<>(
                ingredient,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /ingredients/:id -> delete the "id" ingredient.
     */
    @RequestMapping(value = "/ingredients/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        log.debug("REST request to delete Ingredient : {}", id);
        ingredientRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("ingredient", id.toString())).build();
    }
}
