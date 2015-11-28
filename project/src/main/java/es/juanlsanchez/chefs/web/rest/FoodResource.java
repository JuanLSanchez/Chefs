package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Food;
import es.juanlsanchez.chefs.repository.FoodRepository;
import es.juanlsanchez.chefs.service.FoodService;
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

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing Food.
 */
@RestController
@RequestMapping("/api")
public class FoodResource {

    private final Logger log = LoggerFactory.getLogger(FoodResource.class);

    @Inject
    private FoodRepository foodRepository;

    @Autowired
    private FoodService foodService;

    /**
     * POST  /foods -> Create a new food.
     */
    @RequestMapping(value = "/foods",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Food> createFood(@Valid @RequestBody Food food) throws URISyntaxException {
        log.debug("REST request to save Food : {}", food);
        if (food.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new food cannot already have an ID").body(null);
        }
        Food result = foodRepository.save(food);
        return ResponseEntity.created(new URI("/api/foods/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("food", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /foods -> Updates an existing food.
     */
    @RequestMapping(value = "/foods",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Food> updateFood(@Valid @RequestBody Food food) throws URISyntaxException {
        log.debug("REST request to update Food : {}", food);
        if (food.getId() == null) {
            return createFood(food);
        }
        Food result = foodRepository.save(food);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("food", food.getId().toString()))
                .body(result);
    }

    /**
     * GET  /foods -> get all the foods.
     */
    @RequestMapping(value = "/foods",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Food>> getAllFoods(Pageable pageable)
        throws URISyntaxException {
        Page<Food> page = foodRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/foods");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /foods/:id -> get the "id" food.
     */
    @RequestMapping(value = "/foods/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Food> getFood(@PathVariable Long id) {
        log.debug("REST request to get Food : {}", id);
        return Optional.ofNullable(foodRepository.findOne(id))
            .map(food -> new ResponseEntity<>(
                food,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /foods/search/:name -> get the "id" food.
     */
    @RequestMapping(value = "/foods/search/{name}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Set<Food>> searchFood(@PathVariable String name) {
        log.debug("REST request to search Food : {}", name);
        Set<Food> foods;
        ResponseEntity result;

        foods = foodService.search(name);

        result = Optional.ofNullable(foods)
            .map(food -> new ResponseEntity<>(
                food,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        return result;
    }

    /**
     * DELETE  /foods/:id -> delete the "id" food.
     */
    @RequestMapping(value = "/foods/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFood(@PathVariable Long id) {
        log.debug("REST request to delete Food : {}", id);
        foodRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("food", id.toString())).build();
    }
}
