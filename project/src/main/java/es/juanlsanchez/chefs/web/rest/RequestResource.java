package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Request;
import es.juanlsanchez.chefs.service.RequestService;
import es.juanlsanchez.chefs.web.rest.dto.RequestInfoDTO;
import es.juanlsanchez.chefs.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * REST controller for managing Request.
 */
@RestController
@RequestMapping("/api")
public class RequestResource {

    private final Logger log = LoggerFactory.getLogger(RequestResource.class);

    @Inject
    private RequestService requestService;

    /**
     * PUT  /requests/follower/:followed -> Create o update a request as follower.
     */
    @RequestMapping(value = "/requests/follower/{followed}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Request> createRequest(@PathVariable String followed) throws URISyntaxException {
        log.debug("REST request to save Request as follower: {}", followed);
        ResponseEntity<Request> result;
        try{
            Assert.notNull(followed);
            Request request = requestService.update(followed);
            result = ResponseEntity.created(new URI("/api/requests/follower/" + followed))
                .headers(HeaderUtil.createEntityCreationAlert("request", followed))
                .body(request);
        }catch (IllegalArgumentException e){
            result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    /**
     * PUT  /requests/followed/:follower -> Update a request as followed.
     */

    @RequestMapping(value = "/requests/followed/{follower}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Request> updateRequest(@Valid @RequestBody String status, @PathVariable String follower)
        throws URISyntaxException {
        log.debug("REST request to save Request as followed: {}", follower);
        ResponseEntity<Request> result;
        try{
            Assert.notNull(follower);
            Request request = requestService.update(follower, status);
            result = ResponseEntity.created(new URI("/api/requests/followed/" + follower))
                .headers(HeaderUtil.createEntityCreationAlert("request", follower))
                .body(request);
        }catch (IllegalArgumentException e){
            result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    /**
     * GET  /requests/follower/:followed -> get the request with the principal as follower and followed.
     */
    @RequestMapping(value = "/requests/follower/{followed}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Request> findRequestWithPrincipalAsFollowerAndFollowed(@PathVariable String followed) {
        log.debug("REST request to get Request with principal as follower and followed : {}", followed);
        return Optional.ofNullable(requestService.findRequestWithPrincipalAsFollowerAndFollowed(followed))
            .map(request -> new ResponseEntity<>(
                request,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /requests/followed/:follower -> get the followers of the followed.
     */
    @RequestMapping(value = "/requests/followed/{follower}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Request> findRequestWithPrincipalAsFollowedAndFollower(@PathVariable String follower) {
        log.debug("REST request to get Request with principal as follower and followed : {}", follower);
        return Optional.ofNullable(requestService.findRequestWithPrincipalAsFollowedAndFollower(follower))
            .map(request -> new ResponseEntity<>(
                request,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /requests/count/:login -> get the number of followers and followed.
     */
    @RequestMapping(value = "/requests/count/{login}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RequestInfoDTO> getCountFollowersAndFollowed(@PathVariable String login) {
        log.debug("REST request to get the number of followers and followed : {}", login);
        return Optional.ofNullable(requestService.getCountFollowersAndFollowed(login))
            .map(requestInfoDTO -> new ResponseEntity<>(
                requestInfoDTO,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
