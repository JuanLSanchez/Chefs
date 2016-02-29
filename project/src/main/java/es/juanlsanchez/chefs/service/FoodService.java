package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Food;
import es.juanlsanchez.chefs.repository.FoodRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by juanlu on 28/11/15.
 */
@Service
@Transactional
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    public Page<Food> search(String name, Pageable pageable) {
        Page<Food> result;
        String normalizaedName;

        normalizaedName = normaliza(name);

        result = foodRepository.search("%" + normalizaedName + "%", pageable);

        return result;
    }

    public static String normaliza(String name){
        String result;

        result = StringUtils.stripAccents(name);
        result = result.replaceAll(" ", "");
        result = result.toUpperCase();

        return result;
    }

    public Food save(Food food) {
        Food result;
        String normalizaedName;

        normalizaedName = normaliza(food.getName());
        result = findOneByNormalizaedNameAndName(normalizaedName, food.getName());

        if (result == null){
            food.setNormalizaedName(normalizaedName);
            result = foodRepository.save(food);
        }

        return result;
    }

    public Food findOneByNormalizaedNameAndName(String normalizaedName, String name) {
        Food result;

        result = foodRepository.findOneByNormalizaedNameAndName(normalizaedName, name);

        return result;
    }


}
