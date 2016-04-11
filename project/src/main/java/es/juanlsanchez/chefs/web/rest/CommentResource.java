package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Comment;
import es.juanlsanchez.chefs.service.CommentService;
import es.juanlsanchez.chefs.web.rest.dto.CommentDTO;
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

/**
 * REST controller for managing Comment.
 */
@RestController
@RequestMapping("/api")
public class CommentResource {

    private final Logger log = LoggerFactory.getLogger(CommentResource.class);

    @Inject
    private CommentService commentService;

    /**
     * POST  /comments/{recipeId} -> Create a new comment.
     */
    @RequestMapping(value = "/comments/{socialEntityId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CommentDTO> createComment(@RequestBody String body,
                                                 @PathVariable Long socialEntityId) throws URISyntaxException {
        log.debug("REST request to save body : {}, socialentityId {}", body, socialEntityId);
        ResponseEntity<CommentDTO> result;
        try{
            CommentDTO comment = commentService.create(body, socialEntityId);
            result = ResponseEntity.created(new URI("/api/comments/" + comment.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("comment", comment.getId().toString()))
                .body(comment);
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
     * PUT  /comments/{commentId} -> Updates an existing comment.
     */
    @RequestMapping(value = "/comments/{commentId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<CommentDTO> updateComment(@RequestBody String body,
                                                 @PathVariable Long commentId) throws URISyntaxException {
        log.debug("REST request to update body : {}, commentId {}",
            body, commentId);
        ResponseEntity<CommentDTO> result;
        CommentDTO comment;
        try{
            comment = commentService.update(commentId, body);
            result = ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("comment", comment.getId().toString()))
                .body(comment);
        }catch (IllegalArgumentException e){
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * GET  /comments/{socialEntityId} -> get the comments of a recipe.
     */
    @RequestMapping(value = "/comments/{socialEntityId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CommentDTO>> listComments(@PathVariable Long socialEntityId, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get Comments in socialEntityId: {}", socialEntityId);
        ResponseEntity<List<CommentDTO>> result;
        Page<CommentDTO> page;
        HttpHeaders headers;

        try{
            page = commentService.findAllBySocialEntityId(socialEntityId, pageable);
            headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/comments");
            result = new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }

    /**
     * DELETE  /comments/:id -> delete the "id" comment.
     */
    @RequestMapping(value = "/comments/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.debug("REST request to delete Comment : {}", id);
        ResponseEntity<Void> result;

        try {
            commentService.delete(id);
            result = ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("comment", id.toString())).build();
        }catch (IllegalArgumentException e){
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return result;
    }
}
