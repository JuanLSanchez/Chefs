package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Recipe entity.
 */
public interface RecipeRepository extends JpaRepository<Recipe,Long> {

    @Query("select recipe from Recipe recipe where recipe.user.login = ?#{principal.username} order by recipe.updateDate desc")
    List<Recipe> findByUserIsCurrentUser();

    @Query("select recipe from Recipe recipe where recipe.user.login = ?#{principal.username} order by recipe.updateDate desc")
    Page<Recipe> findByUserIsCurrentUser(Pageable pageable);

    @Query("select recipe from Recipe recipe where recipe.user.id = ?1 order by recipe.updateDate desc")
    Page<Recipe> findByUser(Long id, Pageable pageable);
}
