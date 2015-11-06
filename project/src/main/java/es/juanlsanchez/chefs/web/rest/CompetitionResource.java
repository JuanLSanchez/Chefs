package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Competition;
import es.juanlsanchez.chefs.repository.CompetitionRepository;
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
 * REST controller for managing Competition.
 */
@RestController
@RequestMapping("/api")
public class CompetitionResource {

    private final Logger log = LoggerFactory.getLogger(CompetitionResource.class);

    @Inject
    private CompetitionRepository competitionRepository;

    /**
     * POST  /competitions -> Create a new competition.
     */
    @RequestMapping(value = "/competitions",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Competition> createCompetition(@Valid @RequestBody Competition competition) throws URISyntaxException {
        log.debug("REST request to save Competition : {}", competition);
        if (competition.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new competition cannot already have an ID").body(null);
        }
        Competition result = competitionRepository.save(competition);
        return ResponseEntity.created(new URI("/api/competitions/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("competition", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /competitions -> Updates an existing competition.
     */
    @RequestMapping(value = "/competitions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Competition> updateCompetition(@Valid @RequestBody Competition competition) throws URISyntaxException {
        log.debug("REST request to update Competition : {}", competition);
        if (competition.getId() == null) {
            return createCompetition(competition);
        }
        Competition result = competitionRepository.save(competition);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("competition", competition.getId().toString()))
                .body(result);
    }

    /**
     * GET  /competitions -> get all the competitions.
     */
    @RequestMapping(value = "/competitions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Competition>> getAllCompetitions(Pageable pageable)
        throws URISyntaxException {
        Page<Competition> page = competitionRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/competitions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /competitions/:id -> get the "id" competition.
     */
    @RequestMapping(value = "/competitions/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Competition> getCompetition(@PathVariable Long id) {
        log.debug("REST request to get Competition : {}", id);
        return Optional.ofNullable(competitionRepository.findOneWithEagerRelationships(id))
            .map(competition -> new ResponseEntity<>(
                competition,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /competitions/:id -> delete the "id" competition.
     */
    @RequestMapping(value = "/competitions/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteCompetition(@PathVariable Long id) {
        log.debug("REST request to delete Competition : {}", id);
        competitionRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("competition", id.toString())).build();
    }
}
