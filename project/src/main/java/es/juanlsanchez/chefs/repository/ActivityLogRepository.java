package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.ActivityLog;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ActivityLog entity.
 */
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {

}
