package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.StepPicture;
import es.juanlsanchez.chefs.repository.StepPictureRepository;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing StepPicture.
 */
@RestController
@RequestMapping("/api")
public class StepPictureResource {

    private final Logger log = LoggerFactory.getLogger(StepPictureResource.class);

    @Inject
    private StepPictureRepository stepPictureRepository;

    /**
     * POST  /stepPictures -> Create a new stepPicture.
     */
    @RequestMapping(value = "/stepPictures",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StepPicture> createStepPicture(@RequestBody StepPicture stepPicture) throws URISyntaxException {
        log.debug("REST request to save StepPicture : {}", stepPicture);
        if (stepPicture.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new stepPicture cannot already have an ID").body(null);
        }
        StepPicture result = stepPictureRepository.save(stepPicture);
        return ResponseEntity.created(new URI("/api/stepPictures/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("stepPicture", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /stepPictures -> Updates an existing stepPicture.
     */
    @RequestMapping(value = "/stepPictures",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StepPicture> updateStepPicture(@RequestBody StepPicture stepPicture) throws URISyntaxException {
        log.debug("REST request to update StepPicture : {}", stepPicture);
        if (stepPicture.getId() == null) {
            return createStepPicture(stepPicture);
        }
        StepPicture result = stepPictureRepository.save(stepPicture);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("stepPicture", stepPicture.getId().toString()))
                .body(result);
    }

    /**
     * GET  /stepPictures -> get all the stepPictures.
     */
    @RequestMapping(value = "/stepPictures",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StepPicture>> getAllStepPictures(Pageable pageable)
        throws URISyntaxException {
        Page<StepPicture> page = stepPictureRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stepPictures");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /stepPictures/:id -> get the "id" stepPicture.
     */
    @RequestMapping(value = "/stepPictures/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StepPicture> getStepPicture(@PathVariable Long id) {
        log.debug("REST request to get StepPicture : {}", id);
        return Optional.ofNullable(stepPictureRepository.findOne(id))
            .map(stepPicture -> new ResponseEntity<>(
                stepPicture,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /stepPictures/:id -> delete the "id" stepPicture.
     */
    @RequestMapping(value = "/stepPictures/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStepPicture(@PathVariable Long id) {
        log.debug("REST request to delete StepPicture : {}", id);
        stepPictureRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("stepPicture", id.toString())).build();
    }
}
