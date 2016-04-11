package es.juanlsanchez.chefs.service;

import com.google.common.collect.Lists;
import es.juanlsanchez.chefs.domain.Comment;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.CommentRepository;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.service.util.ErrorMessageService;
import es.juanlsanchez.chefs.web.rest.dto.CommentDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
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
    public CommentDTO create(String body, Long socialEntityId) {
        Comment result;
        SocialEntity socialEntity;
        User user;

        Assert.hasLength(body, ErrorMessageService.ILLEGAL_COMMENT);

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

        return new CommentDTO(result);
    }

    public CommentDTO update(Long commentId, String body) {
        Comment result;
        String principalLogin;

        principalLogin = SecurityUtils.getCurrentLogin();

        result = Optional.ofNullable(commentRepository.findOne(commentId))
            .filter(c -> c.getUser().getLogin().equals(principalLogin))
            .orElseThrow(()->new IllegalArgumentException(ErrorMessageService.ILLEGAL_COMMENT));

        result.setBody(body);

        result = commentRepository.save(result);

        return new CommentDTO(result);
    }

    public Page<CommentDTO> findAllBySocialEntityId(Long socialEntityId, Pageable pageable) {
        SocialEntity socialEntity;

        socialEntity = Optional.ofNullable(socialEntityRepository.findOne(socialEntityId))
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));

        if (!socialEntityIsVisible(socialEntity)){
            throw new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY);
        }
        Page<CommentDTO> result;
        List<CommentDTO> commentDTOList;
        Page<Comment> comments;

        comments = commentRepository.findAllBySocialEntityIdOrderByCreationMomentDesc(socialEntityId, pageable);

        commentDTOList = Lists.newArrayList();

        for(Comment comment:comments){
            commentDTOList.add(new CommentDTO(comment));
        }

        result = new PageImpl<CommentDTO>(commentDTOList,
            new PageRequest(comments.getNumber(),comments.getSize(),comments.getSort()),
            comments.getTotalElements() );

        return result;
    }

    public void delete(Long id) {
        String principalLogin;

        principalLogin = SecurityUtils.getCurrentLogin();

        Optional.ofNullable(commentRepository.findOne(id))
            .filter(c -> c.getUser().getLogin().equals(principalLogin))
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_COMMENT));

        commentRepository.delete(id);

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
}
