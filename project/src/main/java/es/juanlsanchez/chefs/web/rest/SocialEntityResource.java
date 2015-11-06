package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;
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
 * REST controller for managing SocialEntity.
 */
@RestController
@RequestMapping("/api")
public class SocialEntityResource {

    private final Logger log = LoggerFactory.getLogger(SocialEntityResource.class);

    @Inject
    private SocialEntityRepository socialEntityRepository;

    /**
     * POST  /socialEntitys -> Create a new socialEntity.
     */
    @RequestMapping(value = "/socialEntitys",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SocialEntity> createSocialEntity(@RequestBody SocialEntity socialEntity) throws URISyntaxException {
        log.debug("REST request to save SocialEntity : {}", socialEntity);
        if (socialEntity.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new socialEntity cannot already have an ID").body(null);
        }
        SocialEntity result = socialEntityRepository.save(socialEntity);
        return ResponseEntity.created(new URI("/api/socialEntitys/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("socialEntity", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /socialEntitys -> Updates an existing socialEntity.
     */
    @RequestMapping(value = "/socialEntitys",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SocialEntity> updateSocialEntity(@RequestBody SocialEntity socialEntity) throws URISyntaxException {
        log.debug("REST request to update SocialEntity : {}", socialEntity);
        if (socialEntity.getId() == null) {
            return createSocialEntity(socialEntity);
        }
        SocialEntity result = socialEntityRepository.save(socialEntity);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("socialEntity", socialEntity.getId().toString()))
                .body(result);
    }

    /**
     * GET  /socialEntitys -> get all the socialEntitys.
     */
    @RequestMapping(value = "/socialEntitys",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SocialEntity>> getAllSocialEntitys(Pageable pageable, @RequestParam(required = false) String filter)
        throws URISyntaxException {
        if ("event-is-null".equals(filter)) {
            log.debug("REST request to get all SocialEntitys where event is null");
            return new ResponseEntity<>(StreamSupport
                .stream(socialEntityRepository.findAll().spliterator(), false)
                .filter(socialEntity -> socialEntity.getEvent() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        
        if ("recipe-is-null".equals(filter)) {
            log.debug("REST request to get all SocialEntitys where recipe is null");
            return new ResponseEntity<>(StreamSupport
                .stream(socialEntityRepository.findAll().spliterator(), false)
                .filter(socialEntity -> socialEntity.getRecipe() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        
        if ("competition-is-null".equals(filter)) {
            log.debug("REST request to get all SocialEntitys where competition is null");
            return new ResponseEntity<>(StreamSupport
                .stream(socialEntityRepository.findAll().spliterator(), false)
                .filter(socialEntity -> socialEntity.getCompetition() == null)
                .collect(Collectors.toList()), HttpStatus.OK);
        }
        
        Page<SocialEntity> page = socialEntityRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/socialEntitys");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /socialEntitys/:id -> get the "id" socialEntity.
     */
    @RequestMapping(value = "/socialEntitys/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SocialEntity> getSocialEntity(@PathVariable Long id) {
        log.debug("REST request to get SocialEntity : {}", id);
        return Optional.ofNullable(socialEntityRepository.findOneWithEagerRelationships(id))
            .map(socialEntity -> new ResponseEntity<>(
                socialEntity,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /socialEntitys/:id -> delete the "id" socialEntity.
     */
    @RequestMapping(value = "/socialEntitys/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSocialEntity(@PathVariable Long id) {
        log.debug("REST request to delete SocialEntity : {}", id);
        socialEntityRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("socialEntity", id.toString())).build();
    }
}
