package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Step;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.RecipeRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private StepService stepService;
    @Autowired
    private SocialEntityService socialEntityService;

    public Recipe save(Recipe recipe){
        User principal;
        DateTime currentTime;
        Set<Step> steps;
        Recipe result, oldRecipe;

        currentTime = new DateTime();
        principal = userService.getPrincipal();

        if(recipe.getId() == null || recipe.getId() == 0){
            /* Receta nueva */
            recipe.setCreationDate(currentTime);
        }else{
            /* Receta ya creada */
            oldRecipe = findOne(recipe.getId());
            if(!oldRecipe.getUser().equals(principal)){
                isCloneable(recipe, oldRecipe);
                recipe.setId(null);
                recipe.setFather(oldRecipe);
            }
        }

        recipe.setUpdateDate(currentTime);
        recipe.setUser(principal);

        /*Se vacian los pasos*/
        steps = recipe.getSteps();
        recipe.setSteps(null);
        /* Se crea la entidad social */
        recipe.setSocialEntity(socialEntityService.save(recipe.getSocialEntity()));

        result = recipeRepository.save(recipe);

        /*Se le asigna la receta a los pasos y se guardan*/
        if (steps != null && !steps.isEmpty())
            steps.forEach(step -> step.setRecipe(recipe));
        steps = stepService.save(steps);
        result.setSteps(steps);

        return result;

    }

    public Recipe findOne(Long id) {
        Recipe result;

        result = recipeRepository.findOne(id);

        isVisible(result);

        return result;
    }

    private void isVisible(Recipe recipe) {
        User principal;
        boolean isVisible;

        isVisible = recipe.getSocialEntity().getIsPublic();

        if(!isVisible){
            principal = userService.getPrincipal();
            if(!principal.equals(recipe.getUser())){
        /* Si no es publica se debe de revisar que te tiene como seguidor*/
                isVisible = recipe.getUser().getAcceptRequests().stream().
                    filter(request -> request.getFollower().equals(principal)).findFirst().isPresent();
            }else{
                isVisible=true;
            }
        }

        Assert.isTrue(isVisible);
    }

    private void isCloneable(Recipe recipe, Recipe oldRecipe) {
        boolean isCloneable;

        /* Para que se pueda clonar, debe de ser clonable y visible para ese usuario*/
        isCloneable = oldRecipe.getSocialEntity().getPublicInscription() != null &&
            oldRecipe.getSocialEntity().getPublicInscription();

        Assert.isTrue(isCloneable);
    }

    public Page<Recipe> findByUserIsCurrentUser(Pageable pageable) {
        Page<Recipe> result;

        result = recipeRepository.findByUserIsCurrentUser(pageable);

        return result;
    }
}
