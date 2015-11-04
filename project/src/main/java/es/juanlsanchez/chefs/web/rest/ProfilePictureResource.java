package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.ProfilePicture;
import es.juanlsanchez.chefs.repository.ProfilePictureRepository;
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
 * REST controller for managing ProfilePicture.
 */
@RestController
@RequestMapping("/api")
public class ProfilePictureResource {

    private final Logger log = LoggerFactory.getLogger(ProfilePictureResource.class);

    @Inject
    private ProfilePictureRepository profilePictureRepository;

    /**
     * POST  /profilePictures -> Create a new profilePicture.
     */
    @RequestMapping(value = "/profilePictures",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProfilePicture> createProfilePicture(@RequestBody ProfilePicture profilePicture) throws URISyntaxException {
        log.debug("REST request to save ProfilePicture : {}", profilePicture);
        if (profilePicture.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new profilePicture cannot already have an ID").body(null);
        }
        ProfilePicture result = profilePictureRepository.save(profilePicture);
        return ResponseEntity.created(new URI("/api/profilePictures/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("profilePicture", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /profilePictures -> Updates an existing profilePicture.
     */
    @RequestMapping(value = "/profilePictures",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProfilePicture> updateProfilePicture(@RequestBody ProfilePicture profilePicture) throws URISyntaxException {
        log.debug("REST request to update ProfilePicture : {}", profilePicture);
        if (profilePicture.getId() == null) {
            return createProfilePicture(profilePicture);
        }
        ProfilePicture result = profilePictureRepository.save(profilePicture);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("profilePicture", profilePicture.getId().toString()))
                .body(result);
    }

    /**
     * GET  /profilePictures -> get all the profilePictures.
     */
    @RequestMapping(value = "/profilePictures",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ProfilePicture>> getAllProfilePictures(Pageable pageable)
        throws URISyntaxException {
        Page<ProfilePicture> page = profilePictureRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/profilePictures");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /profilePictures/:id -> get the "id" profilePicture.
     */
    @RequestMapping(value = "/profilePictures/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProfilePicture> getProfilePicture(@PathVariable Long id) {
        log.debug("REST request to get ProfilePicture : {}", id);
        return Optional.ofNullable(profilePictureRepository.findOne(id))
            .map(profilePicture -> new ResponseEntity<>(
                profilePicture,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /profilePictures/:id -> delete the "id" profilePicture.
     */
    @RequestMapping(value = "/profilePictures/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteProfilePicture(@PathVariable Long id) {
        log.debug("REST request to delete ProfilePicture : {}", id);
        profilePictureRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("profilePicture", id.toString())).build();
    }
}
