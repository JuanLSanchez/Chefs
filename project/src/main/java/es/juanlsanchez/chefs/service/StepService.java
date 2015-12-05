package es.juanlsanchez.chefs.service;

import com.google.common.collect.Sets;
import es.juanlsanchez.chefs.domain.*;
import es.juanlsanchez.chefs.repository.StepRepository;
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
        Step result, oldStep;
        Set<StepPicture> stepPictures, oldStepPicturesToRemove;
        Set<Ingredient> ingredients, oldIngredientsToRemove ;

        stepPictures = step.getStepPicture();
        ingredients = step.getIngredients();

        if(step.getId()!=null){
            oldStep = stepRepository.findOne(step.getId());
            /* Remove old ingredients */
            oldIngredientsToRemove = Sets.newHashSet(oldStep.getIngredients());
            oldIngredientsToRemove.removeAll(ingredients);
            ingredientService.delete(oldIngredientsToRemove);
            /* Remove old pictures */
            oldStepPicturesToRemove = Sets.newHashSet(oldStep.getStepPicture());
            oldStepPicturesToRemove.removeAll(stepPictures);
            stepPictureService.delete(oldStepPicturesToRemove);
        }

        step.setIngredients(Sets.newHashSet());
        step.setStepPicture(Sets.newHashSet());

        result = stepRepository.save(step);

        /* Save pictures */
        if (stepPictures != null && !stepPictures.isEmpty()){
            stepPictures.forEach(stepPicture -> stepPicture.setStep(result));
            result.setStepPicture(stepPictureService.save(stepPictures));
        }

        /* Save ingredients */
        if (ingredients != null && !ingredients.isEmpty()){
            ingredients.forEach(ingredient -> ingredient.setStep(result));
            result.setIngredients(ingredientService.save(ingredients));
        }

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
