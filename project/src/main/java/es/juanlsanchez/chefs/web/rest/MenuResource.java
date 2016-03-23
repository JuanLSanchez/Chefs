package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Menu;
import es.juanlsanchez.chefs.service.MenuService;
import es.juanlsanchez.chefs.web.rest.dto.MenuDTO;
import es.juanlsanchez.chefs.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Menu.
 */
@RestController
@RequestMapping("/api")
public class MenuResource {

    private final Logger log = LoggerFactory.getLogger(MenuResource.class);

    @Inject
    private MenuService menuService;

    /**
     * POST  /menus/{scheduleId} -> Create a new menu.
     */
    @RequestMapping(value = "/menus/{scheduleId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MenuDTO> createMenu(@Valid @RequestBody Menu menu,
                                           @PathVariable Long scheduleId) throws URISyntaxException {
        log.debug("REST request to save Menu : {}", menu);
        ResponseEntity<MenuDTO> result;
        MenuDTO menuResult;
        if (menu.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new menu cannot already have an ID").body(null);
        }else{
            try{
                menuResult = menuService.create(menu, scheduleId);
                result = ResponseEntity.created(new URI("/api/menus/" + menuResult.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert("menu", menuResult.getId().toString()))
                    .body(menuResult);
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
     * PUT  /menus/{scheduleId} -> Updates an existing menu.
     */
    @RequestMapping(value = "/menus/{scheduleId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MenuDTO> updateMenu(@Valid @RequestBody Menu menu,
                                           @PathVariable Long scheduleId) throws URISyntaxException {
        log.debug("REST request to update Menu : {}", menu);
        ResponseEntity<MenuDTO> result;
        MenuDTO menuResult;
        if (menu.getId() == null) {
            return createMenu(menu, scheduleId);
        }else{
            try{
                menuResult = menuService.update(menu, scheduleId);
                result = ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert("menu", menuResult.getId().toString()))
                    .body(menuResult);
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
     * GET  /menus/{scheduleId} -> get all the menus.
     */
    @RequestMapping(value = "/menus/{scheduleId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<MenuDTO>> getAllMenus(@PathVariable Long scheduleId)
        throws URISyntaxException {
        List<MenuDTO> list;
        ResponseEntity<List<MenuDTO>> result;
        try{
            list = menuService.findAllByScheduleId(scheduleId);
            result = new ResponseEntity<>(list, HttpStatus.OK);
        }catch (IllegalArgumentException e){
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception: " + e.getMessage()).body(null);
        }catch (Throwable e){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    /**
     * DELETE  /menus/:id -> delete the "id" menu.
     */
    @RequestMapping(value = "/menus/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        log.debug("REST request to delete Menu : {}", id);
        ResponseEntity<Void> result;
        try{
            menuService.delete(id);
            result = ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("menu", id.toString())).build();
        }catch (IllegalArgumentException e){
            result = ResponseEntity.badRequest()
                .header("Illegal argument exception:" + e.getMessage()).body(null);
        }catch (Throwable e ){
            result = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
