package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Schedule;
import es.juanlsanchez.chefs.repository.ScheduleRepository;
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
 * REST controller for managing Schedule.
 */
@RestController
@RequestMapping("/api")
public class ScheduleResource {

    private final Logger log = LoggerFactory.getLogger(ScheduleResource.class);

    @Inject
    private ScheduleRepository scheduleRepository;

    /**
     * POST  /schedules -> Create a new schedule.
     */
    @RequestMapping(value = "/schedules",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody Schedule schedule) throws URISyntaxException {
        log.debug("REST request to save Schedule : {}", schedule);
        if (schedule.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new schedule cannot already have an ID").body(null);
        }
        Schedule result = scheduleRepository.save(schedule);
        return ResponseEntity.created(new URI("/api/schedules/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("schedule", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /schedules -> Updates an existing schedule.
     */
    @RequestMapping(value = "/schedules",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Schedule> updateSchedule(@Valid @RequestBody Schedule schedule) throws URISyntaxException {
        log.debug("REST request to update Schedule : {}", schedule);
        if (schedule.getId() == null) {
            return createSchedule(schedule);
        }
        Schedule result = scheduleRepository.save(schedule);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("schedule", schedule.getId().toString()))
                .body(result);
    }

    /**
     * GET  /schedules -> get all the schedules.
     */
    @RequestMapping(value = "/schedules",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Schedule>> getAllSchedules(Pageable pageable)
        throws URISyntaxException {
        Page<Schedule> page = scheduleRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/schedules");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /schedules/:id -> get the "id" schedule.
     */
    @RequestMapping(value = "/schedules/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Schedule> getSchedule(@PathVariable Long id) {
        log.debug("REST request to get Schedule : {}", id);
        return Optional.ofNullable(scheduleRepository.findOne(id))
            .map(schedule -> new ResponseEntity<>(
                schedule,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /schedules/:id -> delete the "id" schedule.
     */
    @RequestMapping(value = "/schedules/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        log.debug("REST request to delete Schedule : {}", id);
        scheduleRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("schedule", id.toString())).build();
    }
}
