package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Tag entity.
 */
public interface TagRepository extends JpaRepository<Tag,Long> {

    @Query("select t from Tag t where t.name like ?1")
    Page<Tag> findAllByNameContains(String name, Pageable pageable);
}
