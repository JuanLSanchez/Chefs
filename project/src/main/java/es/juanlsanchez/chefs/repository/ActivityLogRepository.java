package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.ActivityLog;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the ActivityLog entity.
 */
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {

    @Modifying
    @Query("update ActivityLog activity set activity.objectId = NULL where activity.objectId=?1 AND activity.objectType=?2")
    void dropObjectId(Long id, String objectType);
}
