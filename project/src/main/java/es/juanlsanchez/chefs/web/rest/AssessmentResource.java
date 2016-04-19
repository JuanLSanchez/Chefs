package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.service.AssessmentService;
import es.juanlsanchez.chefs.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URISyntaxException;

/**
 * REST controller for managing Assessment.
 */
@RestController
@RequestMapping("/api")
public class AssessmentResource {

    private final Logger log = LoggerFactory.getLogger(AssessmentResource.class);

    @Inject
    private AssessmentService assessmentService;

    /**
     * PUT  /assessments/{socialEntityId} -> Updates an existing assessment.
     */
    @RequestMapping(value = "/assessments/{socialEntityId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Double> updateAssessment(@Valid @RequestBody Integer assessment,
                                                    @PathVariable Long socialEntityId) throws URISyntaxException {
        log.debug("REST request to update Assessment : {}", assessment);
        ResponseEntity<Double> result;
        Double assessmentValue;

        try{
            assessmentValue = assessmentService.save(socialEntityId, assessment);
            result = ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("assessment", assessmentValue.toString()))
                .body(assessmentValue);
        }catch (IllegalArgumentException e){
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * GET  /assessments/{socialEntityId} -> get the avg of rating for a socialentity.
     */
    @RequestMapping(value = "/assessments/{socialEntityId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Double> getAllAssessments(@PathVariable Long socialEntityId)
        throws URISyntaxException {
        log.debug("REST request to get Assessment of social entity: {}", socialEntityId);
        ResponseEntity<Double> result;
        Double rating;

        try{
            rating = assessmentService.findRatingBySocialEntityId(socialEntityId);
            result = new ResponseEntity<>(rating, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * GET  /assessments/user/{socialEntityId} -> get the "rating" assessment in a social entity by principal.
     */
    @RequestMapping(value = "/assessments/user/{socialEntityId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Integer> getAssessment(@PathVariable Long socialEntityId) {
        log.debug("REST request to get Assessment of user: {}", socialEntityId);
        ResponseEntity<Integer> result;
        Integer rating;

        try{
            rating = assessmentService.findRatingOfUserBySocialEntityId(socialEntityId);
            if(rating==null)rating=-1;
            result = new ResponseEntity<>(rating, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * DELETE  /assessments/user/{socialEntityId} -> delete the assessment in a social entity by principal.
     */
    @RequestMapping(value = "/assessments/user/{socialEntityId}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Double> deleteAssessment(@PathVariable Long socialEntityId) {
        log.debug("REST request to delete Assessment of social entity: {}", socialEntityId);
        ResponseEntity<Double> result;
        Double sumRating;

        try{
            sumRating = assessmentService.deleteBySocialEntityId(socialEntityId);
            result = ResponseEntity.ok()
                .headers(HeaderUtil.createEntityDeletionAlert("assessment", socialEntityId.toString()))
                .body(sumRating);
        }catch (IllegalArgumentException e){
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }
}
