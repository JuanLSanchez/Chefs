package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO;
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

    @Query("select recipe from Recipe recipe where recipe.user.login = ?1 order by recipe.updateDate desc")
    Page<Recipe> findByUserLogin(String login, Pageable pageable);


    /**
     * Find all recipes of a user, that the principal can view
     * @param login The user of the recipes
     * @param pageable Pagination type
     * @return All the login's recipes that the principal can view
     */
    @Query("select recipe from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.user.login=?1" +
        "   and ( recipe.user.login=?#{ principal.username }" +
        "       or (recipe.socialEntity.isPublic=true" +
        "           or (select request " +
        "               from Request request " +
        "               where request.accepted=true " +
        "               and request.followed.login=?#{ principal.username } " +
        "               and request.follower.login=?1) is not null))" +
        "   order by recipe.updateDate desc")
    Page<Recipe> findAllByLoginAndIsVisibility(String login, Pageable pageable);

    /**
     * Find all recipes of a user, that the anonymous can view
     * @param login The user of the recipes
     * @param pageable Pagination type
     * @return All the login's recipes that the anonymous can view
     */
    @Query("select recipe from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.user.login=?1" +
        "   and recipe.socialEntity.isPublic=true" +
        "   order by recipe.updateDate desc")
    Page<Recipe> findAllByLoginAndIsVisibilityForAnonymous(String login, Pageable pageable);

    /**
     * Find all recipes of a user, that the principal can view
     * @param login The user of the recipes
     * @param pageable Pagination type
     * @return All the login's recipes that the principal can view
     */
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe) from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.user.login=?1" +
        "   and ( recipe.user.login=?#{ principal.username }" +
        "       or (recipe.socialEntity.isPublic=true" +
        "           or (select request " +
        "               from Request request " +
        "               where request.accepted=true " +
        "               and request.followed.login=?#{ principal.username } " +
        "               and request.follower.login=?1) is not null))" +
        "   order by recipe.updateDate desc")
    Page<RecipeMiniDTO> findDTOAllByLoginAndIsVisibility(String login, Pageable pageable);

    /**
     * Find all recipes of a user, that the anonymous can view
     * @param login The user of the recipes
     * @param pageable Pagination type
     * @return All the login's recipes that the anonymous can view
     */
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe) from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.user.login=?1" +
        "   and recipe.socialEntity.isPublic=true" +
        "   order by recipe.updateDate desc")
    Page<RecipeMiniDTO> findDTOAllByLoginAndIsVisibilityForAnonymous(String login, Pageable pageable);

    /**
     * Find all recipes where name like the name param and the principal can display
     * @param name  Pattern to find recipes
     * @param pageable Pagination type
     * @return All the recipes where name like the name param and the principal can display
     */
    @Query("select recipe from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.name like ?1" +
        "   and ( recipe.user.login=?#{principal.username}" +
        "       or (recipe.socialEntity.isPublic=true" +
        "           or (select request " +
        "               from Request request " +
        "               where request.accepted=true " +
        "               and request.followed.login=?#{principal.username} " +
        "               and request.follower.login=?1) is not null))" +
        "   order by recipe.updateDate desc")
    Page<Recipe> findAllIsVisibilityAndLikeName(String name, Pageable pageable);

    /**
     * Find all recipes where name like the name parameter and are visible.
     * @param name  Pattern to find recipes
     * @param pageable Pagination type
     * @return All the recipes where name like the name parameter and are visible.
     */
    @Query("select recipe from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.name like ?1" +
        "   and recipe.socialEntity.isPublic=true" +
        "   order by recipe.updateDate desc")
    Page<Recipe> findAllIsVisibilityForAnonymousAndLikeName(String name, Pageable pageable);


    /**
     * Find all recipes where name like the name param and the principal can display
     * @param name  Pattern to find recipes
     * @param pageable Pagination type
     * @return All the recipes where name like the name param and the principal can display
     */
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe) from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.name like ?1" +
        "   and ( recipe.user.login=?#{principal.username}" +
        "       or (recipe.socialEntity.isPublic=true" +
        "           or (select request " +
        "               from Request request " +
        "               where request.accepted=true " +
        "               and request.followed.login=?#{principal.username} " +
        "               and request.follower.login=?1) is not null))" +
        "   order by recipe.updateDate desc")
    Page<RecipeMiniDTO> findDTOAllIsVisibilityAndLikeName(String name, Pageable pageable);

    /**
     * Find all recipes where name like the name parameter and are visible.
     * @param name  Pattern to find recipes
     * @param pageable Pagination type
     * @return All the recipes where name like the name parameter and are visible.
     */
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe) from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.name like ?1" +
        "   and recipe.socialEntity.isPublic=true" +
        "   order by recipe.updateDate desc")
    Page<RecipeMiniDTO> findDTOAllIsVisibilityForAnonymousAndLikeName(String name, Pageable pageable);
}
