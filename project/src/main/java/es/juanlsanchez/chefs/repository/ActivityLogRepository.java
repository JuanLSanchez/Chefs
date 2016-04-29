package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.ActivityLog;
import es.juanlsanchez.chefs.web.rest.dto.ActivityLogDTO;
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

    @Query("select new es.juanlsanchez.chefs.web.rest.dto.ActivityLogDTO(a) from ActivityLog a where a.login in ?1 order by moment desc ")
    Page<ActivityLogDTO> getActivityLogByLogin(List<String> logins, Pageable pageable);

    @Query("select new es.juanlsanchez.chefs.web.rest.dto.ActivityLogDTO(a) from ActivityLog a where a.login = ?1 order by moment desc ")
    Page<ActivityLogDTO> findAllByLoginOrderByMomentDesc(String login, Pageable pageable);
}
