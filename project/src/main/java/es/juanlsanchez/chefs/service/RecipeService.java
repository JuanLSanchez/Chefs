package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Step;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.RecipeRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.web.rest.dto.RecipeMiniDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
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
                recipe = recipe.copy();
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
            steps.forEach(step -> step.setRecipe(result));
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

        isVisible = recipe.getSocialEntity().getIsPublic()&&!recipe.getSocialEntity().getBlocked();

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


    public Page<Recipe> findByUser(Long id, Pageable pageable) {
        Page<Recipe> result;

        result = recipeRepository.findByUser(id, pageable);

        return result;
    }

    public Page<Recipe> findAll(Pageable pageable) {
        Page<Recipe> result;

        result = recipeRepository.findAll(pageable);

        return result;
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public void saveAndFlush(Recipe recipe) {
        recipeRepository.saveAndFlush(recipe);
    }

    public Page<Recipe> findByUserLogin(String login, Pageable pageable) {
        Page<Recipe> result;

        result =recipeRepository.findByUserLogin(login, pageable);

        return result;
    }

    public Page<Recipe> findAllByLoginAndVisibility(String login, Pageable pageable){
        return recipeRepository.findAllByLoginAndIsVisibility(login, pageable);
    }

    public Page<Recipe> findAllByLoginAndIsVisibility(String login, Pageable pageable) {
        Page<Recipe> result;
        if(SecurityUtils.isAuthenticated()){
            result = recipeRepository.findAllByLoginAndIsVisibility(login, pageable);
        }else{
            result = recipeRepository.findAllByLoginAndIsVisibilityForAnonymous(login, pageable);
        }
        return result;
    }

    public Page<Recipe> findAllIsVisibilityAndLikeName(String name, Pageable pageable){
        Page<Recipe> result;
        name = "%"+name+"%";
        if(SecurityUtils.isAuthenticated()){
            result = recipeRepository.findAllIsVisibilityAndLikeName(name, pageable);
        }else{
            result = recipeRepository.findAllIsVisibilityForAnonymousAndLikeName(name, pageable);
        }
        return result;
    }

    public Page<RecipeMiniDTO> findDTOAllByLoginAndIsVisibility(String login, Pageable pageable) {
        Page<RecipeMiniDTO> result;
        if(SecurityUtils.isAuthenticated()){
            result = recipeRepository.findDTOAllByLoginAndIsVisibility(login, pageable);
        }else{
            result = recipeRepository.findDTOAllByLoginAndIsVisibilityForAnonymous(login, pageable);
        }
        return result;
    }

    public Page<RecipeMiniDTO> findDTOAllIsVisibilityAndLikeName(String name, Pageable pageable){
        Page<RecipeMiniDTO> result;
        name = "%"+name+"%";
        if(SecurityUtils.isAuthenticated()){
            result = recipeRepository.findDTOAllIsVisibilityAndLikeName(name, pageable);
        }else{
            result = recipeRepository.findDTOAllIsVisibilityForAnonymousAndLikeName(name, pageable);
        }
        return result;
    }


}
