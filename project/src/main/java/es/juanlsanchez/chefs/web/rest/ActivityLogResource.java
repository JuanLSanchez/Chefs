package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.ActivityLog;
import es.juanlsanchez.chefs.repository.ActivityLogRepository;
import es.juanlsanchez.chefs.service.ActivityLogService;
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
    private ActivityLogService activityLogService;

    /**
     * GET  /activityLogs -> get all the activityLogs.
     */
    @RequestMapping(value = "/activityLogs",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ActivityLog>> getAllActivityLogs(Pageable pageable)
        throws URISyntaxException {
        ResponseEntity<List<ActivityLog>> result;
        HttpHeaders headers;
        Page<ActivityLog> page;

        try {
            page = activityLogService.getPrincipalActivityLog(pageable);
            headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/activityLogs");
            result = new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        } catch (Throwable e) {
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }
}
