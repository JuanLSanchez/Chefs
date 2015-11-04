package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.SocialPicture;
import es.juanlsanchez.chefs.repository.SocialPictureRepository;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for managing SocialPicture.
 */
@RestController
@RequestMapping("/api")
public class SocialPictureResource {

    private final Logger log = LoggerFactory.getLogger(SocialPictureResource.class);

    @Inject
    private SocialPictureRepository socialPictureRepository;

    /**
     * POST  /socialPictures -> Create a new socialPicture.
     */
    @RequestMapping(value = "/socialPictures",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SocialPicture> createSocialPicture(@RequestBody SocialPicture socialPicture) throws URISyntaxException {
        log.debug("REST request to save SocialPicture : {}", socialPicture);
        if (socialPicture.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new socialPicture cannot already have an ID").body(null);
        }
        SocialPicture result = socialPictureRepository.save(socialPicture);
        return ResponseEntity.created(new URI("/api/socialPictures/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("socialPicture", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /socialPictures -> Updates an existing socialPicture.
     */
    @RequestMapping(value = "/socialPictures",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SocialPicture> updateSocialPicture(@RequestBody SocialPicture socialPicture) throws URISyntaxException {
        log.debug("REST request to update SocialPicture : {}", socialPicture);
        if (socialPicture.getId() == null) {
            return createSocialPicture(socialPicture);
        }
        SocialPicture result = socialPictureRepository.save(socialPicture);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("socialPicture", socialPicture.getId().toString()))
                .body(result);
    }

    /**
     * GET  /socialPictures -> get all the socialPictures.
     */
    @RequestMapping(value = "/socialPictures",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SocialPicture>> getAllSocialPictures(Pageable pageable, @RequestParam(required = false) String filter)
        throws URISyntaxException {
        if ("socialentity-is-null".equals(filter)) {
            log.debug("REST request to get all SocialPictures where socialEntity is null");
            return new ResponseEntity<>(StreamSupport
                .stream(socialPictureRepository.findAll().spliterator(), false)
                .filter(socialPicture -> socialPicture.getSocialEntity() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        
        Page<SocialPicture> page = socialPictureRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/socialPictures");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /socialPictures/:id -> get the "id" socialPicture.
     */
    @RequestMapping(value = "/socialPictures/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SocialPicture> getSocialPicture(@PathVariable Long id) {
        log.debug("REST request to get SocialPicture : {}", id);
        return Optional.ofNullable(socialPictureRepository.findOne(id))
            .map(socialPicture -> new ResponseEntity<>(
                socialPicture,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /socialPictures/:id -> delete the "id" socialPicture.
     */
    @RequestMapping(value = "/socialPictures/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSocialPicture(@PathVariable Long id) {
        log.debug("REST request to delete SocialPicture : {}", id);
        socialPictureRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("socialPicture", id.toString())).build();
    }
}
