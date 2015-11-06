package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Score;
import es.juanlsanchez.chefs.repository.ScoreRepository;
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
 * REST controller for managing Score.
 */
@RestController
@RequestMapping("/api")
public class ScoreResource {

    private final Logger log = LoggerFactory.getLogger(ScoreResource.class);

    @Inject
    private ScoreRepository scoreRepository;

    /**
     * POST  /scores -> Create a new score.
     */
    @RequestMapping(value = "/scores",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Score> createScore(@Valid @RequestBody Score score) throws URISyntaxException {
        log.debug("REST request to save Score : {}", score);
        if (score.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new score cannot already have an ID").body(null);
        }
        Score result = scoreRepository.save(score);
        return ResponseEntity.created(new URI("/api/scores/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("score", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /scores -> Updates an existing score.
     */
    @RequestMapping(value = "/scores",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Score> updateScore(@Valid @RequestBody Score score) throws URISyntaxException {
        log.debug("REST request to update Score : {}", score);
        if (score.getId() == null) {
            return createScore(score);
        }
        Score result = scoreRepository.save(score);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("score", score.getId().toString()))
                .body(result);
    }

    /**
     * GET  /scores -> get all the scores.
     */
    @RequestMapping(value = "/scores",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Score>> getAllScores(Pageable pageable)
        throws URISyntaxException {
        Page<Score> page = scoreRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scores");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /scores/:id -> get the "id" score.
     */
    @RequestMapping(value = "/scores/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Score> getScore(@PathVariable Long id) {
        log.debug("REST request to get Score : {}", id);
        return Optional.ofNullable(scoreRepository.findOne(id))
            .map(score -> new ResponseEntity<>(
                score,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /scores/:id -> delete the "id" score.
     */
    @RequestMapping(value = "/scores/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteScore(@PathVariable Long id) {
        log.debug("REST request to delete Score : {}", id);
        scoreRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("score", id.toString())).build();
    }
}
