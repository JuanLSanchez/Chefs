package es.juanlsanchez.chefs.service;

import com.google.common.collect.Sets;
import es.juanlsanchez.chefs.domain.*;
import es.juanlsanchez.chefs.repository.RecipeRepository;
import es.juanlsanchez.chefs.repository.StepRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@Transactional
public class StepService {

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private StepPictureService stepPictureService;

    @Autowired
    private IngredientService ingredientService;

    public Step save(Step step){
        Step result;
        Set<StepPicture> stepPictures;
        Set<Ingredient> ingredients;

        stepPictures = step.getStepPicture();
        ingredients = step.getIngredients();

        step.setIngredients(Sets.newConcurrentHashSet());
        step.setStepPicture(Sets.newConcurrentHashSet());

        result = stepRepository.save(step);

        if (stepPictures != null && !stepPictures.isEmpty())
            stepPictures.forEach(stepPicture -> stepPictures.add(stepPictureService.save(stepPicture)));
        if (ingredients != null && !ingredients.isEmpty())
            ingredients.forEach(ingredient -> ingredients.add(ingredientService.save(ingredient)));

        result.setStepPicture(stepPictures);
        result.setIngredients(ingredients);

        return result;
    }

    public Set<Step> save(Set<Step> steps){
        Set<Step> result;

        result = Sets.newConcurrentHashSet();

        steps.forEach(step -> result.add(save(step)));

        return result;
    }

}
