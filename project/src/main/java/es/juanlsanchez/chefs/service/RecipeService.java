package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Authority;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Step;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.AuthorityRepository;
import es.juanlsanchez.chefs.repository.PersistentTokenRepository;
import es.juanlsanchez.chefs.repository.RecipeRepository;
import es.juanlsanchez.chefs.repository.UserRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.service.util.RandomUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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

    public Recipe create(Recipe recipe){
        User principal;
        DateTime currentTime;
        Set<Step> steps;

        currentTime = new DateTime();
        principal = userService.getPrincipal();
        steps = recipe.getSteps();

        recipe.setSteps(null);

        recipe.setUser(principal);
        recipe.setCreationDate(currentTime);
        recipe.setUpdateDate(currentTime);

        Recipe result = recipeRepository.save(recipe);

        if (steps != null && !steps.isEmpty())
            steps.forEach(step -> step.setRecipe(recipe));

        steps = stepService.save(steps);

        result.setSteps(steps);

        return result;

    }

}
