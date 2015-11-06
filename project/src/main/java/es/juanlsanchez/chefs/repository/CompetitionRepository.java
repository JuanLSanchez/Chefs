package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Competition;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Competition entity.
 */
public interface CompetitionRepository extends JpaRepository<Competition,Long> {

    @Query("select competition from Competition competition where competition.owner.login = ?#{principal.username}")
    List<Competition> findByOwnerIsCurrentUser();

    @Query("select distinct competition from Competition competition left join fetch competition.recipes left join fetch competition.users")
    List<Competition> findAllWithEagerRelationships();

    @Query("select competition from Competition competition left join fetch competition.recipes left join fetch competition.users where competition.id =:id")
    Competition findOneWithEagerRelationships(@Param("id") Long id);

}
