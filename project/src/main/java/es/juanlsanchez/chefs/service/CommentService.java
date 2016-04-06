package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Comment;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.CommentRepository;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.service.util.ErrorMessageService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * chefs
 * Created by juanlu on 06-abr-2016.
 */
@Service
@Transactional
public class CommentService {


    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SocialEntityRepository socialEntityRepository;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;

    /* Simple CRUD*/
    public Comment create(String body, Long socialEntityId) {
        Comment result;
        SocialEntity socialEntity;
        User user;

        Assert.notNull(body);

        socialEntity = Optional.ofNullable(socialEntityRepository.findOne(socialEntityId))
            .filter(this::socialEntityIsVisible)
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));

        user = userService.getPrincipal();

        result = new Comment();
        result.setBody(body);
        result.setCreationMoment(new DateTime());
        result.setSocialEntity(socialEntity);
        result.setUser(user);

        result = commentRepository.save(result);

        return result;
    }

    public Comment update(Long commentId, String body) {
        Comment result;
        String principalLogin;

        principalLogin = SecurityUtils.getCurrentLogin();

        result = Optional.ofNullable(commentRepository.findOne(commentId))
            .filter(c -> c.getUser().getLogin().equals(principalLogin))
            .orElseThrow(()->new IllegalArgumentException(ErrorMessageService.ILLEGAL_COMMENT));

        result.setBody(body);

        result = commentRepository.save(result);

        return result;
    }

    /* Others */

    /* Utilities */
    private boolean socialEntityIsVisible(SocialEntity s) {
        boolean result;

        result = s.getIsPublic() && !s.getBlocked();

        if(!result){
            if(s.getRecipe() != null){
                result = recipeService.findOne(s.getRecipe().getId()) != null;
            }
        }

        return result;
    }

    public Page<Comment> findAllBySocialEntityId(Long socialEntityId, Pageable pageable) {
        if (!socialEntityIsVisible(socialEntityRepository.findOne(socialEntityId))){
            throw new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY);
        }
        Page<Comment> result;

        result = commentRepository.findAllBySocialEntityIdOrderByCreationMomentDesc(socialEntityId, pageable);

        return result;
    }

    public void delete(Long id) {
        String principalLogin;

        principalLogin = SecurityUtils.getCurrentLogin();

        Optional.ofNullable(commentRepository.findOne(id))
            .filter(c -> c.getUser().getLogin().equals(principalLogin))
            .orElseThrow(()->new IllegalArgumentException(ErrorMessageService.ILLEGAL_COMMENT));

        commentRepository.delete(id);

    }
}
