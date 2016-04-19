package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.SocialEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the SocialEntity entity.
 */
public interface SocialEntityRepository extends JpaRepository<SocialEntity,Long> {

    @Query("select distinct socialEntity from SocialEntity socialEntity left join fetch socialEntity.tags left join fetch socialEntity.users")
    List<SocialEntity> findAllWithEagerRelationships();

    @Query("select socialEntity from SocialEntity socialEntity left join fetch socialEntity.tags left join fetch socialEntity.users where socialEntity.id =:id")
    SocialEntity findOneWithEagerRelationships(@Param("id") Long id);

    
}
