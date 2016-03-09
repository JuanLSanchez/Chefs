package es.juanlsanchez.chefs.web.rest;

import com.codahale.metrics.annotation.Timed;
import es.juanlsanchez.chefs.domain.Authority;
import es.juanlsanchez.chefs.domain.BackgroundPicture;
import es.juanlsanchez.chefs.domain.ProfilePicture;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.AuthorityRepository;
import es.juanlsanchez.chefs.security.AuthoritiesConstants;
import es.juanlsanchez.chefs.service.UserService;
import es.juanlsanchez.chefs.web.rest.dto.ManagedUserDTO;
import es.juanlsanchez.chefs.web.rest.dto.UserDTO;
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
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UserService userService;

    /**
     * POST  /users -> Create a new user.
     */
    @RequestMapping(value = "/users",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<User> createUser(@RequestBody User user) throws URISyntaxException {
        log.debug("REST request to save User : {}", user);
        if (user.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new user cannot already have an ID").body(null);
        }
        User result = userService.save(user);
        return ResponseEntity.created(new URI("/api/users/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("user", result.getId().toString()))
                .body(result);
    }

    /**
     * PUT  /users -> Updates an existing User.
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<ManagedUserDTO> updateUser(@RequestBody ManagedUserDTO managedUserDTO) throws URISyntaxException {
        log.debug("REST request to update User : {}", managedUserDTO);
        ProfilePicture profilePicture;
        BackgroundPicture backgroundPicture;

        profilePicture = new ProfilePicture();
        profilePicture.setSrc(managedUserDTO.getProfilePicture());

        backgroundPicture = new BackgroundPicture();
        backgroundPicture.setSrc(managedUserDTO.getBackgroundPicture());

        return Optional.of(userService
            .findOne(managedUserDTO.getId()))
            .map(user -> {
                user.setLogin(managedUserDTO.getLogin());
                user.setFirstName(managedUserDTO.getFirstName());
                user.setLastName(managedUserDTO.getBiography());
                user.setEmail(managedUserDTO.getEmail());
                user.setActivated(managedUserDTO.isActivated());
                user.setLangKey(managedUserDTO.getLangKey());
                Set<Authority> authorities = user.getAuthorities();
                authorities.clear();
                managedUserDTO.getAuthorities().stream().forEach(
                    authority -> authorities.add(authorityRepository.findOne(authority))
                );
                user.setProfilePicture(profilePicture);
                user.setBackgroundPicture(backgroundPicture);
                return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert("user", managedUserDTO.getLogin()))
                    .body(new ManagedUserDTO(userService
                        .findOne(managedUserDTO.getId())));
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * GET  /users -> get all users.
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<ManagedUserDTO>> getAllUsers(Pageable pageable)
        throws URISyntaxException {
        Page<ManagedUserDTO> page;
        page = userService.findAllUsersConvertToManagedUserDto(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    /**
     * GET  /users/likeLoginOrLikeFirstName/:q -> get all users by login.
     */
    @RequestMapping(value = "/users/likeLoginOrLikeFirstName/{q}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<ManagedUserDTO>> getAllUsersByLogin(@PathVariable String q, Pageable pageable)
        throws URISyntaxException {
        Page<ManagedUserDTO> page;
        page = userService.findAllLikeLoginOrLikeFirstNameConvertToManagedUserDto(q, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/users/{login}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ManagedUserDTO> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return userService
                .findOneUser(login)
                .map(userDTO -> new ResponseEntity<>(userDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /users/followers/:followed -> get users who follow the login user.
     */
    @RequestMapping(value = "/users/followers/{followed}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<UserDTO>> findAllFollowersByLogin (@PathVariable String followed, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get the followers: {}", followed);
        Page<UserDTO> page;
        page = userService.findAllFollowersByLogin(followed, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users/followers/"+followed);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /users/following/:follower -> get users who the login user following.
     */
    @RequestMapping(value = "/users/following/{follower}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<UserDTO>> findAllFollowingByLogin (@PathVariable String follower, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get the following: {}", follower);
        Page<UserDTO> page;
        page = userService.findAllFollowingByLogin(follower, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/users/followers/" + follower);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
