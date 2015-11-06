package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Schedule;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Schedule entity.
 */
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {

    @Query("select schedule from Schedule schedule where schedule.user.login = ?#{principal.username}")
    List<Schedule> findByUserIsCurrentUser();

}
