package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Schedule;
import es.juanlsanchez.chefs.service.ScheduleService;
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

/**
 * REST controller for managing Schedule.
 */
@RestController
@RequestMapping("/api")
public class ScheduleResource {

    private final Logger log = LoggerFactory.getLogger(ScheduleResource.class);

    @Inject
    private ScheduleService scheduleService;

    /**
     * POST  /schedules -> Create a new schedule.
     */
    @RequestMapping(value = "/schedules",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody Schedule schedule) throws URISyntaxException {
        log.debug("REST request to save Schedule : {}", schedule);
        ResponseEntity<Schedule> result;
        Schedule scheduleResult;
        if (schedule.getId() != null) {
            result = ResponseEntity.badRequest().header("Failure", "A new schedule cannot already have an ID").body(null);
        }else{
            try{
                scheduleResult = scheduleService.create(schedule);
                result = ResponseEntity.created(new URI("/api/schedules/" + scheduleResult.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("schedule", scheduleResult.getId().toString()))
                    .body(scheduleResult);
            }catch (IllegalArgumentException e){
                result = ResponseEntity.badRequest()
                    .header("Illegal argument exception:" + e.getMessage()).body(null);
            }catch (Throwable e ){
                result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return result;
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
        Schedule scheduleResult;
        ResponseEntity<Schedule> result;
        if (schedule.getId() == null) {
            result = createSchedule(schedule);
        }else {
            try {
                scheduleResult = scheduleService.update(schedule);
                result = ResponseEntity.ok()
                        .headers(HeaderUtil.createEntityUpdateAlert("schedule", schedule.getId().toString()))
                        .body(scheduleResult);
            }catch (IllegalArgumentException e){
                result = ResponseEntity.badRequest()
                    .header("Illegal argument exception:" + e.getMessage()).body(null);
            }catch (Throwable e){
                result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }
        return  result;
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
        ResponseEntity<List<Schedule>> result;
        Page<Schedule> page;
        HttpHeaders headers;
        try{
            page = scheduleService.findAllByUserIsCurrentUser(pageable);
            headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/schedules");
            result =  new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception: " + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
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
        return scheduleService.findOne(id)
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
        ResponseEntity<Void> result;
        try{
            scheduleService.delete(id);
            result = ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("schedule", id.toString())).build();
        }catch (IllegalArgumentException e){
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception: " + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
