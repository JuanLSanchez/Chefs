package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ActivityLog entity.
 */
public interface ActivityLogRepository extends JpaRepository<ActivityLog,Long> {

    @Modifying
    @Query("update ActivityLog activity set activity.objectId = NULL where activity.objectId=?1 AND activity.objectType=?2")
    void dropObjectId(Long id, String objectType);

    @Query("select a from ActivityLog a where a.login in ?1 order by moment desc ")
    Page<ActivityLog> getActivityLogByLogin(List<String> logins, Pageable pageable);
}
