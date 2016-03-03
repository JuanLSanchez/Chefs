package es.juanlsanchez.chefs.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
        List<Step> toRemove = Lists.newArrayList();
        Recipe result, oldRecipe=null;
        boolean make = false;

        currentTime = new DateTime();
        principal = userService.getPrincipal();

        if(recipe.getId() == null || recipe.getId() == 0){
            /* Receta nueva */
            recipe.setCreationDate(currentTime);
            make = true;
        }else{
            /* Receta ya creada */
            oldRecipe = findOne(recipe.getId());
            if (oldRecipe != null){
                make = oldRecipe.getUser().equals(principal);
                if(!make && isCloneable(oldRecipe)){
                    recipe = recipe.copy();
                    make = true;
                }
            }

        }
        if(make){
        /*Se  borran las sobrantes */
            if( oldRecipe !=null ){
                toRemove.addAll(oldRecipe.getSteps());
                toRemove.removeAll(recipe.getSteps());
                toRemove.removeIf(step -> step.getId()==null);
                stepService.remove(toRemove);
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
        }else{
            result = null;
        }

        return result;

    }

    public Recipe findOne(Long id) {
        Recipe result;

        result = recipeRepository.findOne(id);

        if(!isVisible(result)){
            result = null;
        }

        return result;
    }

    private boolean isVisible(Recipe recipe) {
        boolean result;

        // Check is not null
        if ( recipe != null){
            // Check is public
            if( !recipe.getSocialEntity().getIsPublic()
                || recipe.getSocialEntity().getBlocked()){
                final User principal = userService.getPrincipal();
                // Check the principal
                if ( ! recipe.getUser().equals(principal) ){
                    if ( recipe.getSocialEntity().getBlocked() ){
                        // The recipe is blocked
                        result = false;
                    }else{
                        // Check the followed
                        result = recipe.getUser().getAcceptRequests().stream()
                            .filter(r -> r.getAccepted())
                            .filter(r -> r.getFollower().equals(principal))
                            .findFirst().isPresent();
                    }
                }else{
                    // The principal is the owner
                    result = true;
                }
            }else{
                // The recipe is public
                result = true;
            }
        }else{
            // Not exist recipe
            result = false;
        }

        return result;
    }

    private boolean isCloneable(Recipe recipe) {
        boolean isCloneable;

        /* Para que se pueda clonar, debe de ser clonable y visible para ese usuario*/
        isCloneable = recipe.getSocialEntity().getPublicInscription() != null &&
            recipe.getSocialEntity().getPublicInscription();

        return  isCloneable;
    }

    public Page<Recipe> findByUserIsCurrentUser(Pageable pageable) {
        Page<Recipe> result;

        result = recipeRepository.findByUserIsCurrentUser(pageable);

        return result;
    }

    public Page<RecipeMiniDTO> findDTOByUserIsCurrentUser(Pageable pageable) {
        Page<RecipeMiniDTO> result;

        result = recipeRepository.findDTOByUserIsCurrentUser(pageable);

        return result;
    }

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Page<Recipe> findAllByLoginAndIsVisibility(String login, Pageable pageable) {
        Page<Recipe> result;
        boolean authenticated;

        try{
            authenticated = SecurityUtils.isAuthenticated();
        }catch (NullPointerException nfe){
            authenticated = false;
        }

        if(authenticated){
            result = recipeRepository.findAllByLoginAndIsVisibility(login, pageable);
        }else{
            result = recipeRepository.findAllByLoginAndIsVisibilityForAnonymous(login, pageable);
        }
        return result;
    }

    public Page<RecipeMiniDTO> findDTOAllByLoginAndIsVisibility(String login, Pageable pageable) {
        Page<RecipeMiniDTO> result;
        boolean authenticated;

        try{
            authenticated = SecurityUtils.isAuthenticated();
        }catch (NullPointerException nfe){
            authenticated = false;
        }

        if(authenticated){
            result = recipeRepository.findDTOAllByLoginAndIsVisibility(login, pageable);
        }else{
            result = recipeRepository.findDTOAllByLoginAndIsVisibilityForAnonymous(login, pageable);
        }
        return result;
    }

    public Page<Recipe> findAllIsVisibilityAndLikeName(String name, Pageable pageable){
        Page<Recipe> result;
        boolean authenticated;

        name = "%"+name+"%";

        try{
            authenticated = SecurityUtils.isAuthenticated();
        }catch (NullPointerException nfe){
            authenticated = false;
        }
        if(authenticated){
            result = recipeRepository.findAllIsVisibilityAndLikeName(name, pageable);
        }else{
            result = recipeRepository.findAllIsVisibilityForAnonymousAndLikeName(name, pageable);
        }
        return result;
    }

    public Page<RecipeMiniDTO> findDTOAllIsVisibilityAndLikeName(String name, Pageable pageable){
        Page<RecipeMiniDTO> result;
        boolean authenticated;

        name = "%"+name+"%";

        try{
            authenticated = SecurityUtils.isAuthenticated();
        }catch (NullPointerException nfe){
            authenticated = false;
        }
        if(authenticated){
            result = recipeRepository.findDTOAllIsVisibilityAndLikeName(name, pageable);
        }else{
            result = recipeRepository.findDTOAllIsVisibilityForAnonymousAndLikeName(name, pageable);
        }
        return result;
    }

    public Long countByUserLoginAndSocialEntityBlocked(String login, boolean blocked) {
        return recipeRepository.countByUserLoginAndSocialEntityBlocked(login, blocked);
    }
}
