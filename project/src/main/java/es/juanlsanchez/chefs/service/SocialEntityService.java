package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by juanlu on 5/12/15.
 */
@Service
@Transactional
public class SocialEntityService {

    @Autowired
    private SocialEntityRepository socialEntityRepository;

    @Autowired
    private TagService tagService;

    public SocialEntity save(SocialEntity socialEntity) {
        SocialEntity result;

        /* Se crean las etiquetas que no existan*/
        socialEntity.getTags().forEach(tag -> {
            if (tag.getId() == null || tag.getId() == 0)
                tag = tagService.save(tag);
        });

        result = socialEntityRepository.save(socialEntity);

        return result;
    }
}
