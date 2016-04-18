package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.SocialPicture;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;
import es.juanlsanchez.chefs.service.util.ErrorMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

/**
 * Created by juanlu on 5/12/15.
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

        result = socialEntityRepository.save(socialEntity);

        return result;
    }

    public SocialEntity findOne(Long socialEntityId) {
        SocialEntity result;

        result = Optional.ofNullable(socialEntityRepository.findOne(socialEntityId))
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));

        if(result.getRecipe()!=null){
            Assert.notNull(recipeService.findOne(result.getId()), ErrorMessageService.ILLEGAL_SOCIAL_ENTITY);
        }else{
            throw new IllegalArgumentException(ErrorMessageService.ILLEGAL_TODO);
        }

        return result;
    }
}
