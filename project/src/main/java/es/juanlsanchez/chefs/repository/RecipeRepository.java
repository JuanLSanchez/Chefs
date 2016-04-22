package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Tag;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the Recipe entity.
 */
public interface RecipeRepository extends JpaRepository<Recipe,Long> {

    String FIND_BY_USER_IS_CURRENT_USER = " from Recipe recipe where recipe.user.login = ?#{principal.username} order by recipe.updateDate desc";

    @Query("select recipe"+ FIND_BY_USER_IS_CURRENT_USER)
    Page<Recipe> findByUserIsCurrentUser(Pageable pageable);
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe)"+ FIND_BY_USER_IS_CURRENT_USER)
    Page<RecipeMiniDTO> findDTOByUserIsCurrentUser(Pageable pageable);


    String FIND_ALL_BY_LOGIN_AND_IS_VISIBILITY = " from Recipe recipe " +
        "   where recipe.user.login=?1" +
        "   and ( recipe.user.login=?#{ principal.username }" +
        "       or recipe.socialEntity.blocked=false " +
        "           and (recipe.socialEntity.isPublic=true" +
        "               or (select request " +
        "                   from Request request " +
        "                  where request.accepted=true " +
        "                   and request.followed.login= ?1" +
        "                   and request.follower.login=?#{ principal.username }) is not null))" +
        "   order by recipe.updateDate desc";
    @Query("select recipe"+FIND_ALL_BY_LOGIN_AND_IS_VISIBILITY )
    Page<Recipe> findAllByLoginAndIsVisibility(String login, Pageable pageable);
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe)"+FIND_ALL_BY_LOGIN_AND_IS_VISIBILITY )
    Page<RecipeMiniDTO> findDTOAllByLoginAndIsVisibility(String login, Pageable pageable);


    String FIND_ALL_BY_LOGIN_AND_IS_VISIBILITY_FOR_ANONYMOUS = " from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.user.login=?1" +
        "   and recipe.socialEntity.isPublic=true" +
        "   order by recipe.updateDate desc";
    @Query("select recipe"+FIND_ALL_BY_LOGIN_AND_IS_VISIBILITY_FOR_ANONYMOUS)
    Page<Recipe> findAllByLoginAndIsVisibilityForAnonymous(String login, Pageable pageable);
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe)"+FIND_ALL_BY_LOGIN_AND_IS_VISIBILITY_FOR_ANONYMOUS)
    Page<RecipeMiniDTO> findDTOAllByLoginAndIsVisibilityForAnonymous(String login, Pageable pageable);

    String FIND_ALL_IS_VISIBILITY_AND_LIKE_NAME = " from Recipe recipe " +
        "   where recipe.name like ?1" +
        "   and ( recipe.user.login=?#{ principal.username }" +
        "       or recipe.socialEntity.blocked=false " +
        "           and (recipe.socialEntity.isPublic=true" +
        "               or (select request " +
        "                   from Request request " +
        "                  where request.accepted=true " +
        "                   and request.followed.login= recipe.user.login" +
        "                   and request.follower.login=?#{ principal.username }) is not null))" +
        "   order by recipe.updateDate desc";
    @Query("select recipe"+FIND_ALL_IS_VISIBILITY_AND_LIKE_NAME)
    Page<Recipe> findAllIsVisibilityAndLikeName(String name, Pageable pageable);
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe)"+FIND_ALL_IS_VISIBILITY_AND_LIKE_NAME)
    Page<RecipeMiniDTO> findDTOAllIsVisibilityAndLikeName(String name, Pageable pageable);

    String FIND_ALL_IS_VISIBILITY_FOR_ANONYMOUS_AND_LIKE_NAME = " from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.name like ?1" +
        "   and recipe.socialEntity.isPublic=true" +
        "   order by recipe.updateDate desc";
    @Query("select recipe"+FIND_ALL_IS_VISIBILITY_FOR_ANONYMOUS_AND_LIKE_NAME)
    Page<Recipe> findAllIsVisibilityForAnonymousAndLikeName(String name, Pageable pageable);
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe)"+FIND_ALL_IS_VISIBILITY_FOR_ANONYMOUS_AND_LIKE_NAME)
    Page<RecipeMiniDTO> findDTOAllIsVisibilityForAnonymousAndLikeName(String name, Pageable pageable);

    Long countByUserLoginAndSocialEntityBlocked(String login, boolean blocked);


    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe) from Recipe recipe " +
        "   where ?1 in elements(recipe.socialEntity.tags)" +
        "   and ( recipe.user.login=?#{ principal.username }" +
        "       or recipe.socialEntity.blocked=false " +
        "           and (recipe.socialEntity.isPublic=true" +
        "               or (select request " +
        "                   from Request request " +
        "                  where request.accepted=true " +
        "                   and request.followed.login= recipe.user.login" +
        "                   and request.follower.login=?#{ principal.username }) is not null))" +
        "   order by recipe.updateDate desc")
    Page<RecipeMiniDTO> findAllIsVisibilityAndSocialEntityTagId(Tag tag, Pageable pageable);
    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe) from Recipe recipe " +
        "   where recipe.socialEntity.blocked=false " +
        "   and recipe.socialEntity.isPublic=true" +
        "   and ?1 in elements(recipe.socialEntity.tags)" +
        "   order by recipe.updateDate desc")
    Page<RecipeMiniDTO> findAllIsVisibilityForAnonymousAndSocialEntityTagId(Tag tag, Pageable pageable);


    @Query("select new es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO(recipe) from Recipe recipe " +
        "where ?1 in elements(recipe.socialEntity.users)")
    Page<RecipeMiniDTO> findAllBySocialEntityUserIn(User principal, Pageable pageable);
}
