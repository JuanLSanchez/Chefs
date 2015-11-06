package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.ActivityLog;
import es.juanlsanchez.chefs.repository.ActivityLogRepository;
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
 * REST controller for managing ActivityLog.
 */
@RestController
@RequestMapping("/api")
public class ActivityLogResource {

    private final Logger log = LoggerFactory.getLogger(ActivityLogResource.class);

    @Inject
    private ActivityLogRepository activityLogRepository;

    /**
     * POST  /activityLogs -> Create a new activityLog.
     */
    @RequestMapping(value = "/activityLogs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ActivityLog> createActivityLog(@RequestBody ActivityLog activityLog) throws URISyntaxException {
        log.debug("REST request to save ActivityLog : {}", activityLog);
        if (activityLog.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new activityLog cannot already have an ID").body(null);
        }
        ActivityLog result = activityLogRepository.save(activityLog);
        return ResponseEntity.created(new URI("/api/activityLogs/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("activityLog", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /activityLogs -> Updates an existing activityLog.
     */
    @RequestMapping(value = "/activityLogs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ActivityLog> updateActivityLog(@RequestBody ActivityLog activityLog) throws URISyntaxException {
        log.debug("REST request to update ActivityLog : {}", activityLog);
        if (activityLog.getId() == null) {
            return createActivityLog(activityLog);
        }
        ActivityLog result = activityLogRepository.save(activityLog);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("activityLog", activityLog.getId().toString()))
                .body(result);
    }

    /**
     * GET  /activityLogs -> get all the activityLogs.
     */
    @RequestMapping(value = "/activityLogs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ActivityLog>> getAllActivityLogs(Pageable pageable)
        throws URISyntaxException {
        Page<ActivityLog> page = activityLogRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/activityLogs");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /activityLogs/:id -> get the "id" activityLog.
     */
    @RequestMapping(value = "/activityLogs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ActivityLog> getActivityLog(@PathVariable Long id) {
        log.debug("REST request to get ActivityLog : {}", id);
        return Optional.ofNullable(activityLogRepository.findOne(id))
            .map(activityLog -> new ResponseEntity<>(
                activityLog,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /activityLogs/:id -> delete the "id" activityLog.
     */
    @RequestMapping(value = "/activityLogs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteActivityLog(@PathVariable Long id) {
        log.debug("REST request to delete ActivityLog : {}", id);
        activityLogRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("activityLog", id.toString())).build();
    }
}
