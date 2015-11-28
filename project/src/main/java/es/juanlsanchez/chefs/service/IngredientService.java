package es.juanlsanchez.chefs.service;

import com.google.common.collect.Sets;
import es.juanlsanchez.chefs.domain.Ingredient;
import es.juanlsanchez.chefs.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Created by juanlu on 27/11/15.
 */
@Service
@Transactional
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;


    public Ingredient save(Ingredient ingredient) {
        Ingredient result;

        result = ingredientRepository.save(ingredient);

        return result;
    }

    public Set<Ingredient> save(Set<Ingredient> ingredients) {
        Set<Ingredient> result;

        result = Sets.newHashSet(ingredientRepository.save(ingredients));

        return result;
    }
}
