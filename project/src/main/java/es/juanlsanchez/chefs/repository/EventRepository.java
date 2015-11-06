package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Event;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Event entity.
 */
public interface EventRepository extends JpaRepository<Event,Long> {

    @Query("select event from Event event where event.owner.login = ?#{principal.username}")
    List<Event> findByOwnerIsCurrentUser();

    @Query("select distinct event from Event event left join fetch event.users left join fetch event.recipes")
    List<Event> findAllWithEagerRelationships();

    @Query("select event from Event event left join fetch event.users left join fetch event.recipes where event.id =:id")
    Event findOneWithEagerRelationships(@Param("id") Long id);

}
