package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Tag;
import es.juanlsanchez.chefs.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by juanlu on 5/12/15.
 */
@Service
@Transactional
public class TagService {

    @Autowired
    private TagRepository tagRepository;


    public Page<Tag> findAllByNameContains(String name, Pageable pageable) {
        Page<Tag> result;

        result = tagRepository.findAllByNameContains("%" + name + "%", pageable);

        return result;
    }

    public Tag save(Tag tag) {
        Tag result;

        result = tagRepository.save(tag);

        return result;
    }
}
