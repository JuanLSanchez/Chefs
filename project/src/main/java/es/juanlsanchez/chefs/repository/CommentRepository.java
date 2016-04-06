package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Comment entity.
 */
public interface CommentRepository extends JpaRepository<Comment,Long> {

    Page<Comment> findAllBySocialEntityIdOrderByCreationMomentDesc(Long socialEntityId, Pageable pageable);
}
