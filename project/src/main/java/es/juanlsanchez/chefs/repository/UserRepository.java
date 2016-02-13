package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Authority;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.web.rest.dto.UserDTO;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(DateTime dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByLogin(String login);

    @Query("select u from User u where (u.login like ?1 or u.firstName like ?2) " +
                                " and size(u.authorities)=1 " +
                                " and ?3 in elements(u.authorities)")
    Page<User> findAllLikeLoginOrLikeFirstName(String q, String q1, Authority authority,Pageable pageable);

    @Override
    void delete(User t);

    @Query("select u from User u where size(u.authorities)=1 and ?1 in elements(u.authorities)")
    Page<User> findAllByAuthority(Authority role_user, Pageable pageable);

    @Query("select new es.juanlsanchez.chefs.web.rest.dto.UserDTO(request.follower) " +
            "   from Request request " +
            "   where request.followed.login=?1" +
            "    and (request.accepted=true or (?1=?#{principal.username} and request.ignored=false))")
    Page<UserDTO> findAllFollowersByLogin(String login, Pageable page);

    @Query("select new es.juanlsanchez.chefs.web.rest.dto.UserDTO(request.followed) " +
        "   from Request request " +
        "   where request.follower.login=?1" +
        "    and request.accepted=true")
    Page<UserDTO> findAllFollowingByLogin(String login, Pageable pageable);
}
