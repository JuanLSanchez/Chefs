package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.*;
import es.juanlsanchez.chefs.repository.AuthorityRepository;
import es.juanlsanchez.chefs.repository.PersistentTokenRepository;
import es.juanlsanchez.chefs.repository.RequestRepository;
import es.juanlsanchez.chefs.repository.UserRepository;
import es.juanlsanchez.chefs.security.AuthoritiesConstants;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.service.util.RandomUtil;
import es.juanlsanchez.chefs.web.rest.dto.ManagedUserDTO;
import es.juanlsanchez.chefs.web.rest.dto.UserDTO;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    public static final boolean DEFAULT_ACTIVATE_STATE = false;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private ProfilePictureService profilePictureService;

    @Inject
    private BackgroundPictureService backgroundPictureService;

    @Autowired
    private RequestRepository requestRepository;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                userRepository.save(user);
                log.debug("Activated user: {}", user);
                return user;
            });
        return Optional.empty();
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
       log.debug("Reset user password for reset key {}", key);

       return userRepository.findOneByResetKey(key)
           .filter(user -> {
               DateTime oneDayAgo = DateTime.now().minusHours(24);
               return user.getResetDate().isAfter(oneDayAgo.toInstant().getMillis());
           })
           .map(user -> {
               user.setPassword(passwordEncoder.encode(newPassword));
               user.setResetKey(null);
               user.setResetDate(null);
               userRepository.save(user);
               return user;
           });
    }

    public Optional<User> requestPasswordReset(String mail) {
       return userRepository.findOneByEmail(mail)
           .filter(User::getActivated)
           .map(user -> {
               user.setResetKey(RandomUtil.generateResetKey());
               user.setResetDate(DateTime.now());
               userRepository.save(user);
               return user;
           });
    }

    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
                                      String langKey) {

        User newUser = new User();
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(login);
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        // new user is active
        newUser.setActivated(DEFAULT_ACTIVATE_STATE);
        // new user gets registration key
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        newUser = userRepository.save(newUser);
        // new user add pictures
        newUser.setBackgroundPicture(new BackgroundPicture());
        newUser.getBackgroundPicture().setUser(newUser);
        backgroundPictureService.save(newUser.getBackgroundPicture());
        newUser.setProfilePicture(new ProfilePicture());
        newUser.getProfilePicture().setUser(newUser);
        profilePictureService.save(newUser.getProfilePicture());
        log.debug("Created Information for User: {}", newUser);

        Request request;
        request = new Request();
        request.setAccepted(true);
        request.setFollowed(newUser);
        request.setFollower(newUser);
        request.setCreationDate(new DateTime());
        requestRepository.save(request);

        return newUser;
    }

    public void updateUserInformation(String firstName, String lastName, String email, String langKey,
                                      byte[] profilePicture, byte[] backgroundPicture) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            u.setLangKey(langKey);
            //u.getBackgroundPicture().setSrc(backgroundPicture);
            userRepository.save(u);

            if(u.getProfilePicture() == null){
                u.setProfilePicture(new ProfilePicture());
                u.getProfilePicture().setUser(u);
            }
            u.getProfilePicture().setSrc(profilePicture);
            profilePictureService.save(u.getProfilePicture());

            if(u.getBackgroundPicture() == null){
                u.setBackgroundPicture(new BackgroundPicture());
                u.getBackgroundPicture().setUser(u);
            }
            u.getBackgroundPicture().setSrc(backgroundPicture);
            backgroundPictureService.save(u.getBackgroundPicture());

            log.debug("Changed Information for User: {}", u);
        });
    }

    public void changePassword(String password) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u-> {
            String encryptedPassword = passwordEncoder.encode(password);
            u.setPassword(encryptedPassword);
            userRepository.save(u);
            log.debug("Changed password for User: {}", u);
        });
    }

    @Transactional(readOnly = true)
    public Optional<ManagedUserDTO> findOneUser(String login) {
        return userRepository
            .findOneByLogin(login)
            .filter(user ->
                    user.getAuthorities()
                        .stream()
                        .allMatch(authority ->
                                authority.getName().equals(AuthoritiesConstants.USER)
                        )
            )
            .map(ManagedUserDTO::new);
    }

    @Transactional(readOnly = true)
    public User getUserWithAuthorities() {
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).get();
        user.getAuthorities().size(); // eagerly load the association
        return user;
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = new LocalDate();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).stream().forEach(token -> {
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        DateTime now = new DateTime();
        List<User> users = userRepository.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
        for (User user : users) {
            log.debug("Deleting not activated user {}", user.getLogin());
            userRepository.delete(user);
        }
    }

    public User getPrincipal(){
        String login;
        User result;

        login = SecurityUtils.getCurrentLogin();

        result = login!=null? userRepository.findOneByLogin(login).get():null;

        return result;
    }

    public Page<User> findAllLikeLoginOrLikeFirstName(String q, Pageable pageable) {
        return userRepository.findAllLikeLoginOrLikeFirstName("%"+q+"%", "%"+q+"%",
            authorityRepository.findOne("ROLE_USER"),pageable);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAllByAuthority(authorityRepository.findOne(AuthoritiesConstants.USER), pageable);
    }

    public Optional<User> getUserWithLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

    public Page<UserDTO> findAllFollowersByLogin(String login, Pageable pageable) {
        return userRepository.findAllFollowersByLogin(login, SecurityUtils.getCurrentLogin(), pageable);
    }

    public Page<UserDTO> findAllFollowingByLogin(String login, Pageable pageable) {
        return userRepository.findAllFollowingByLogin(login, pageable);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findOne(Long id) {
        return userRepository.findOne(id);
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<ManagedUserDTO> findAllUsersConvertToManagedUserDto(Pageable pageable) {
        return userRepository.findAllConvertToManagedUserDto(authorityRepository.findOne(AuthoritiesConstants.USER), pageable);
    }
    public Page<ManagedUserDTO> findAllLikeLoginOrLikeFirstNameConvertToManagedUserDto(String q, Pageable pageable) {
        return userRepository.findAllLikeLoginOrLikeFirstNameConvertToManagedUserDto("%" + q + "%", "%" + q + "%",
            authorityRepository.findOne("ROLE_USER"), pageable);
    }
}
