package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Vote;
import es.juanlsanchez.chefs.repository.VoteRepository;
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
 * REST controller for managing Vote.
 */
@RestController
@RequestMapping("/api")
public class VoteResource {

    private final Logger log = LoggerFactory.getLogger(VoteResource.class);

    @Inject
    private VoteRepository voteRepository;

    /**
     * POST  /votes -> Create a new vote.
     */
    @RequestMapping(value = "/votes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Vote> createVote(@RequestBody Vote vote) throws URISyntaxException {
        log.debug("REST request to save Vote : {}", vote);
        if (vote.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new vote cannot already have an ID").body(null);
        }
        Vote result = voteRepository.save(vote);
        return ResponseEntity.created(new URI("/api/votes/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("vote", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /votes -> Updates an existing vote.
     */
    @RequestMapping(value = "/votes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Vote> updateVote(@RequestBody Vote vote) throws URISyntaxException {
        log.debug("REST request to update Vote : {}", vote);
        if (vote.getId() == null) {
            return createVote(vote);
        }
        Vote result = voteRepository.save(vote);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("vote", vote.getId().toString()))
                .body(result);
    }

    /**
     * GET  /votes -> get all the votes.
     */
    @RequestMapping(value = "/votes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Vote>> getAllVotes(Pageable pageable)
        throws URISyntaxException {
        Page<Vote> page = voteRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/votes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /votes/:id -> get the "id" vote.
     */
    @RequestMapping(value = "/votes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Vote> getVote(@PathVariable Long id) {
        log.debug("REST request to get Vote : {}", id);
        return Optional.ofNullable(voteRepository.findOne(id))
            .map(vote -> new ResponseEntity<>(
                vote,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /votes/:id -> delete the "id" vote.
     */
    @RequestMapping(value = "/votes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        log.debug("REST request to delete Vote : {}", id);
        voteRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("vote", id.toString())).build();
    }
}
