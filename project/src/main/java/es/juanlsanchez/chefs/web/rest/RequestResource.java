package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Request;
import es.juanlsanchez.chefs.repository.RequestRepository;
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
 * REST controller for managing Request.
 */
@RestController
@RequestMapping("/api")
public class RequestResource {

    private final Logger log = LoggerFactory.getLogger(RequestResource.class);

    @Inject
    private RequestRepository requestRepository;

    /**
     * POST  /requests -> Create a new request.
     */
    @RequestMapping(value = "/requests",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Request> createRequest(@Valid @RequestBody Request request) throws URISyntaxException {
        log.debug("REST request to save Request : {}", request);
        if (request.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new request cannot already have an ID").body(null);
        }
        Request result = requestRepository.save(request);
        return ResponseEntity.created(new URI("/api/requests/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("request", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /requests -> Updates an existing request.
     */
    @RequestMapping(value = "/requests",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Request> updateRequest(@Valid @RequestBody Request request) throws URISyntaxException {
        log.debug("REST request to update Request : {}", request);
        if (request.getId() == null) {
            return createRequest(request);
        }
        Request result = requestRepository.save(request);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("request", request.getId().toString()))
                .body(result);
    }

    /**
     * GET  /requests -> get all the requests.
     */
    @RequestMapping(value = "/requests",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Request>> getAllRequests(Pageable pageable)
        throws URISyntaxException {
        Page<Request> page = requestRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/requests");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /requests/:id -> get the "id" request.
     */
    @RequestMapping(value = "/requests/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Request> getRequest(@PathVariable Long id) {
        log.debug("REST request to get Request : {}", id);
        return Optional.ofNullable(requestRepository.findOne(id))
            .map(request -> new ResponseEntity<>(
                request,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /requests/:id -> delete the "id" request.
     */
    @RequestMapping(value = "/requests/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        log.debug("REST request to delete Request : {}", id);
        requestRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("request", id.toString())).build();
    }
}
