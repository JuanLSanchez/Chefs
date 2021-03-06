package es.juanlsanchez.chefs.service;

import com.google.common.collect.Sets;
import es.juanlsanchez.chefs.domain.Food;
import es.juanlsanchez.chefs.domain.Ingredient;
import es.juanlsanchez.chefs.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Created by juanlu on 27/11/15.
 */
@Service
@Transactional
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private FoodService foodService;


    public Ingredient save(Ingredient ingredient) {
        Ingredient result;
        Food food;

        food = foodService.save(ingredient.getFood());
        ingredient.setFood(food);

        result = ingredientRepository.save(ingredient);

        return result;
    }

    public Set<Ingredient> save(Set<Ingredient> ingredients) {
        Set<Ingredient> result;

        result = Sets.newHashSet();

        ingredients.forEach( ingredient -> result.add(save(ingredient)));

        return result;
    }

    public void delete(Ingredient ingredient) {
        ingredientRepository.delete(ingredient);
    }

    public void delete(Set<Ingredient> oldIngredientsToRemove) {
        ingredientRepository.delete(oldIngredientsToRemove);
    }

    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }
}
