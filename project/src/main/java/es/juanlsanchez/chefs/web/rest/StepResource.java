package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Step;
import es.juanlsanchez.chefs.repository.StepRepository;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing Step.
 */
@RestController
@RequestMapping("/api")
public class StepResource {

    private final Logger log = LoggerFactory.getLogger(StepResource.class);

    @Inject
    private StepRepository stepRepository;

    /**
     * POST  /steps -> Create a new step.
     */
    @RequestMapping(value = "/steps",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Step> createStep(@Valid @RequestBody Step step) throws URISyntaxException {
        log.debug("REST request to save Step : {}", step);
        if (step.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new step cannot already have an ID").body(null);
        }
        Step result = stepRepository.save(step);
        return ResponseEntity.created(new URI("/api/steps/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("step", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /steps -> Updates an existing step.
     */
    @RequestMapping(value = "/steps",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Step> updateStep(@Valid @RequestBody Step step) throws URISyntaxException {
        log.debug("REST request to update Step : {}", step);
        if (step.getId() == null) {
            return createStep(step);
        }
        Step result = stepRepository.save(step);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("step", step.getId().toString()))
                .body(result);
    }

    /**
     * GET  /steps -> get all the steps.
     */
    @RequestMapping(value = "/steps",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Step>> getAllSteps(Pageable pageable, @RequestParam(required = false) String filter)
        throws URISyntaxException {
        if ("steppicture-is-null".equals(filter)) {
            log.debug("REST request to get all Steps where stepPicture is null");
            return new ResponseEntity<>(StreamSupport
                .stream(stepRepository.findAll().spliterator(), false)
                .filter(step -> step.getStepPicture() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        
        Page<Step> page = stepRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/steps");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /steps/:id -> get the "id" step.
     */
    @RequestMapping(value = "/steps/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Step> getStep(@PathVariable Long id) {
        log.debug("REST request to get Step : {}", id);
        return Optional.ofNullable(stepRepository.findOne(id))
            .map(step -> new ResponseEntity<>(
                step,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /steps/:id -> delete the "id" step.
     */
    @RequestMapping(value = "/steps/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStep(@PathVariable Long id) {
        log.debug("REST request to delete Step : {}", id);
        stepRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("step", id.toString())).build();
    }
}
