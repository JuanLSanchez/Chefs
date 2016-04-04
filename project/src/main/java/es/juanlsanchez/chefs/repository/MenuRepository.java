package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Menu;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Menu entity.
 */
public interface MenuRepository extends JpaRepository<Menu,Long> {

    @Query("select distinct menu from Menu menu left join fetch menu.recipes")
    List<Menu> findAllWithEagerRelationships();

    @Query("select menu from Menu menu left join fetch menu.recipes where menu.id =:id")
    Menu findOneWithEagerRelationships(@Param("id") Long id);

    List<Menu> findAllByScheduleIdOrderByTimeAsc(Long scheduleId);
}
