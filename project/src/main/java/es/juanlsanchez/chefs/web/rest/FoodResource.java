package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Food;
import es.juanlsanchez.chefs.service.FoodService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * REST controller for managing Food.
 */
@RestController
@RequestMapping("/api")
public class FoodResource {

    private final Logger log = LoggerFactory.getLogger(FoodResource.class);

    @Autowired
    private FoodService foodService;


    /**
     * GET  /foods/search/:name -> get the "id" food.
     */
    @RequestMapping(value = "/foods/search/{name}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity searchFood(@PathVariable String name, Pageable pageable)
        throws URISyntaxException, UnsupportedEncodingException {

        log.debug("REST request to search Food : {}", name);
        Page<Food> page;
        HttpHeaders headers;
        ResponseEntity result;

        page = foodService.search(name, pageable);
        headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/foods/search/" + URLEncoder.encode(name, "UTF-8"));
        result = new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);

        return result;
    }
}
