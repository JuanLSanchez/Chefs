package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Food;
import es.juanlsanchez.chefs.repository.FoodRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by juanlu on 28/11/15.
 */
@Service
@Transactional
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    public Set<Food> search(String name) {
        Set<Food> result;
        String normalizaedName;

        normalizaedName = StringUtils.stripAccents(name);
        normalizaedName = normalizaedName.replaceAll(" ", "");
        normalizaedName = normalizaedName.toUpperCase();

        result = foodRepository.search("%" + normalizaedName + "%");

        return result;
    }
}
