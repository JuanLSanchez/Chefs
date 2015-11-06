package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Vote;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Vote entity.
 */
public interface VoteRepository extends JpaRepository<Vote,Long> {

    @Query("select vote from Vote vote where vote.user.login = ?#{principal.username}")
    List<Vote> findByUserIsCurrentUser();

}
