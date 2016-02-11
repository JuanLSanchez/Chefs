package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Request;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Request entity.
 */
public interface RequestRepository extends JpaRepository<Request,Long> {

    @Query("select request from Request request where request.follower.login = ?#{principal.username}")
    List<Request> findByFollowerIsCurrentUser();

    @Query("select request from Request request where request.followed.login = ?#{principal.username}")
    List<Request> findByFollowedIsCurrentUser();

    @Query("select request from Request request " +
            " where request.follower.login = ?#{principal.username} " +
            "   and request.followed.login = ?1")
    Request findRequestWithPrincipalAsFollowerAndFollowed(String followed);


    Long countByFollowedLoginAndAccepted(String login, boolean accepted);

    Long countByFollowerLoginAndAccepted(String login, boolean accepted);

    Long countByFollowedLoginAndAcceptedAndLockedAndIgnored(String login, boolean accepted, boolean locked, boolean ignored);
}
