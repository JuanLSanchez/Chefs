package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.SocialPicture;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.service.util.ErrorMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Created by juanlu on 5/12/15.
 * Service to Social entities
 */
@Service
@Transactional
public class SocialEntityService {

    public static final boolean DEFAULT_IS_PUBLIC = true;
    public static final boolean DEFAULT_PUBLIC_INSCRIPTION = true;
    public static final boolean DEFAULT_BLOCKED = true;
    @Autowired
    private SocialEntityRepository socialEntityRepository;

    @Autowired
    private TagService tagService;
    @Autowired
    private SocialPictureService socialPictureService;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private UserService userService;

    public SocialEntity save(SocialEntity socialEntity) {
        SocialEntity result;

        /* Se crean las etiquetas que no existan*/
        socialEntity.getTags().forEach(tag -> {
            if (tag.getId() == null || tag.getId() == 0)
                tag = tagService.save(tag);
        });

        /* La primera vez se crea la imagen y se inicializa las valoraciones */
        if (socialEntity.getSocialPicture()==null){
            socialEntity.setSocialPicture(socialPictureService.save(new SocialPicture()));
            socialEntity.setSumRating(0);
        }

        /* Se comprueba la seguridad */
        if( socialEntity.getIsPublic() == null ){ socialEntity.setIsPublic(DEFAULT_IS_PUBLIC); }
        if( socialEntity.getPublicInscription() == null ){
            socialEntity.setPublicInscription(DEFAULT_PUBLIC_INSCRIPTION);
        }
        if( socialEntity.getBlocked() == null ){ socialEntity.setBlocked( DEFAULT_BLOCKED ); }

        if(socialEntity.getId()!=null){
            socialEntity.setUsers(socialEntityRepository.findOne(socialEntity.getId()).getUsers());
        }

        socialEntity.setUsers(socialEntity.getUsers());

        result = socialEntityRepository.save(socialEntity);

        return result;
    }

    public SocialEntity findOne(Long socialEntityId) {
        SocialEntity result;

        result = Optional.ofNullable(socialEntityRepository.findOne(socialEntityId))
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));

        if(result.getRecipe()!=null){
            Assert.notNull(recipeService.findOne(result.getRecipe().getId()), ErrorMessageService.ILLEGAL_SOCIAL_ENTITY);
        }else{
            throw new IllegalArgumentException(ErrorMessageService.ILLEGAL_TODO);
        }

        return result;
    }

    public Integer findRatingBySocialEntityId(Long socialEntityId) {
        Integer result;
        SocialEntity socialEntity;

        socialEntity = findOne(socialEntityId);

        result = socialEntity.getUsers().size();

        return result;
    }

    public Boolean findRatingOfUserBySocialEntityId(Long socialEntityId) {
        boolean like;
        User principal;

        principal = userService.getPrincipal();

        like = findOne(socialEntityId).getUsers().contains(principal);

        return like;
    }

    public Boolean update(Long socialEntityId) {
        boolean like;
        SocialEntity socialEntity;
        User principal;

        socialEntity = socialEntityRepository.findOne(socialEntityId);
        principal = userService.getPrincipal();

        like = !socialEntity.getUsers().contains(principal);

        if(like){
            findOne(socialEntityId);
            socialEntity.getUsers().add(principal);
        }else{
            socialEntity.getUsers().remove(principal);
        }

        socialEntityRepository.save(socialEntity);

        return like;
    }
}
