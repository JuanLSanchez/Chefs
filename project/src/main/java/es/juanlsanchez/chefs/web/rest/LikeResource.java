package es.juanlsanchez.chefs.web.rest;


import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.service.SocialEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

/**
 *
 * REST controller for managing Assessment.
 * Created by juanlu on 19-abr-2016.
 */
@RestController
@RequestMapping("/api")
public class LikeResource {

    private final Logger log = LoggerFactory.getLogger(LikeResource.class);

    @Autowired
    private SocialEntityService socialEntityService;

    /**
     * PUT  /likes/{socialEntityId} -> Updates an existing assessment.
     */
    @RequestMapping(value = "/likes/{socialEntityId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> updateLikes(@PathVariable Long socialEntityId) throws URISyntaxException {
        log.debug("REST request to update Likes of socialEntity : {}", socialEntityId);
        ResponseEntity<Boolean> result;
        Boolean likes;

        try{
            likes = socialEntityService.update(socialEntityId);
            result = ResponseEntity.ok()
                .body(likes);
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
     * GET  /likes/{socialEntityId} -> get the avg of rating for a socialEntity.
     */
    @RequestMapping(value = "/likes/{socialEntityId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Integer> getAllLikes(@PathVariable Long socialEntityId)
        throws URISyntaxException {
        log.debug("REST request to get Likes of social entity: {}", socialEntityId);
        ResponseEntity<Integer> result;
        Integer likes;

        try {
            likes = socialEntityService.findRatingBySocialEntityId(socialEntityId);
            result = new ResponseEntity<>(likes, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.debug("Illegal argument exception: {}", e.getMessage());
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        } catch (Throwable e) {
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * GET  /likes/user/{socialEntityId} -> get the avg of rating for a socialEntity.
     */
    @RequestMapping(value = "/likes/user/{socialEntityId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> getLikeUser(@PathVariable Long socialEntityId)
        throws URISyntaxException {
        log.debug("REST request to get Likes of social entity: {}", socialEntityId);
        ResponseEntity<Boolean> result;
        Boolean likes;

        try {
            likes = socialEntityService.findRatingOfUserBySocialEntityId(socialEntityId);
            result = new ResponseEntity<>(likes, HttpStatus.OK);
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
