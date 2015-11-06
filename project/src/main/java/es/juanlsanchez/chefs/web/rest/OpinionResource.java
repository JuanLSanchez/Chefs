package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Opinion;
import es.juanlsanchez.chefs.repository.OpinionRepository;
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
 * REST controller for managing Opinion.
 */
@RestController
@RequestMapping("/api")
public class OpinionResource {

    private final Logger log = LoggerFactory.getLogger(OpinionResource.class);

    @Inject
    private OpinionRepository opinionRepository;

    /**
     * POST  /opinions -> Create a new opinion.
     */
    @RequestMapping(value = "/opinions",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Opinion> createOpinion(@Valid @RequestBody Opinion opinion) throws URISyntaxException {
        log.debug("REST request to save Opinion : {}", opinion);
        if (opinion.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new opinion cannot already have an ID").body(null);
        }
        Opinion result = opinionRepository.save(opinion);
        return ResponseEntity.created(new URI("/api/opinions/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("opinion", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /opinions -> Updates an existing opinion.
     */
    @RequestMapping(value = "/opinions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Opinion> updateOpinion(@Valid @RequestBody Opinion opinion) throws URISyntaxException {
        log.debug("REST request to update Opinion : {}", opinion);
        if (opinion.getId() == null) {
            return createOpinion(opinion);
        }
        Opinion result = opinionRepository.save(opinion);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("opinion", opinion.getId().toString()))
                .body(result);
    }

    /**
     * GET  /opinions -> get all the opinions.
     */
    @RequestMapping(value = "/opinions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Opinion>> getAllOpinions(Pageable pageable)
        throws URISyntaxException {
        Page<Opinion> page = opinionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/opinions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /opinions/:id -> get the "id" opinion.
     */
    @RequestMapping(value = "/opinions/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Opinion> getOpinion(@PathVariable Long id) {
        log.debug("REST request to get Opinion : {}", id);
        return Optional.ofNullable(opinionRepository.findOne(id))
            .map(opinion -> new ResponseEntity<>(
                opinion,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /opinions/:id -> delete the "id" opinion.
     */
    @RequestMapping(value = "/opinions/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteOpinion(@PathVariable Long id) {
        log.debug("REST request to delete Opinion : {}", id);
        opinionRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("opinion", id.toString())).build();
    }
}
