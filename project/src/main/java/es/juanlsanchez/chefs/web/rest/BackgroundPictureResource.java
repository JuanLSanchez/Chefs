package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.BackgroundPicture;
import es.juanlsanchez.chefs.repository.BackgroundPictureRepository;
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
 * REST controller for managing BackgroundPicture.
 */
@RestController
@RequestMapping("/api")
public class BackgroundPictureResource {

    private final Logger log = LoggerFactory.getLogger(BackgroundPictureResource.class);

    @Inject
    private BackgroundPictureRepository backgroundPictureRepository;

    /**
     * POST  /backgroundPictures -> Create a new backgroundPicture.
     */
    @RequestMapping(value = "/backgroundPictures",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BackgroundPicture> createBackgroundPicture(@RequestBody BackgroundPicture backgroundPicture) throws URISyntaxException {
        log.debug("REST request to save BackgroundPicture : {}", backgroundPicture);
        if (backgroundPicture.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new backgroundPicture cannot already have an ID").body(null);
        }
        BackgroundPicture result = backgroundPictureRepository.save(backgroundPicture);
        return ResponseEntity.created(new URI("/api/backgroundPictures/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("backgroundPicture", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /backgroundPictures -> Updates an existing backgroundPicture.
     */
    @RequestMapping(value = "/backgroundPictures",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BackgroundPicture> updateBackgroundPicture(@RequestBody BackgroundPicture backgroundPicture) throws URISyntaxException {
        log.debug("REST request to update BackgroundPicture : {}", backgroundPicture);
        if (backgroundPicture.getId() == null) {
            return createBackgroundPicture(backgroundPicture);
        }
        BackgroundPicture result = backgroundPictureRepository.save(backgroundPicture);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("backgroundPicture", backgroundPicture.getId().toString()))
                .body(result);
    }

    /**
     * GET  /backgroundPictures -> get all the backgroundPictures.
     */
    @RequestMapping(value = "/backgroundPictures",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<BackgroundPicture>> getAllBackgroundPictures(Pageable pageable)
        throws URISyntaxException {
        Page<BackgroundPicture> page = backgroundPictureRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/backgroundPictures");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /backgroundPictures/:id -> get the "id" backgroundPicture.
     */
    @RequestMapping(value = "/backgroundPictures/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BackgroundPicture> getBackgroundPicture(@PathVariable Long id) {
        log.debug("REST request to get BackgroundPicture : {}", id);
        return Optional.ofNullable(backgroundPictureRepository.findOne(id))
            .map(backgroundPicture -> new ResponseEntity<>(
                backgroundPicture,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /backgroundPictures/:id -> delete the "id" backgroundPicture.
     */
    @RequestMapping(value = "/backgroundPictures/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBackgroundPicture(@PathVariable Long id) {
        log.debug("REST request to delete BackgroundPicture : {}", id);
        backgroundPictureRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("backgroundPicture", id.toString())).build();
    }
}
