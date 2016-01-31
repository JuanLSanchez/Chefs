package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Authority;
import es.juanlsanchez.chefs.domain.BackgroundPicture;
import es.juanlsanchez.chefs.domain.ProfilePicture;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.AuthorityRepository;
import es.juanlsanchez.chefs.repository.UserRepository;
import es.juanlsanchez.chefs.security.AuthoritiesConstants;
import es.juanlsanchez.chefs.service.UserService;
import es.juanlsanchez.chefs.web.rest.dto.ManagedUserDTO;
import es.juanlsanchez.chefs.web.rest.dto.MiniUserDTO;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing users.
 *
 * <p>This class accesses the User entity, and needs to fetch its collection of authorities.</p>
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * </p>
 * <p>
 * We use a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </p>
 * <p>Another option would be to have a specific JPA entity graph to handle this case.</p>
 */
@RestController
@RequestMapping("/api/search")
public class SearchResource {

    @Inject
    private UserRepository userRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UserService userService;

    /**
     * GET  /users/:q -> get all users filter by first name and login.
     */
    @RequestMapping(value = "/users/{q}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<MiniUserDTO>> getAllUsersByLoginAndFirstName(@PathVariable String q, Pageable pageable)
        throws URISyntaxException {
        Page<User> users = userService.findAllLikeLoginOrLikeFirstName(q, pageable);
        List<MiniUserDTO> miniUserDTOs = users.getContent().stream()
            .map(user -> new MiniUserDTO(user))
            .collect(Collectors.toList());
        return new ResponseEntity<>(miniUserDTOs, HttpStatus.OK);
    }

    /**
     * GET  /users -> get all users.
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<MiniUserDTO>> getAllUsersByLoginAndFirstName(Pageable pageable)
        throws URISyntaxException {
        Page<User> users = userService.findAll(pageable);
        List<MiniUserDTO> miniUserDTOs = users.getContent().stream()
            .map(user -> new MiniUserDTO(user))
            .collect(Collectors.toList());
        return new ResponseEntity<>(miniUserDTOs, HttpStatus.OK);
    }

}