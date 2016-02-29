package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.Application;
import es.juanlsanchez.chefs.repository.PersistentTokenRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by juanlu on 26/02/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class RecipeServiceTest {

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private RecipeService recipeService;

    @Inject
    private ApplicationContext context;

    @Test
    public void pruebas(){
        recipeService.findAllIsVisibilityAndLikeName("recipe002", new PageRequest(0, 10));
        AuthenticationManager authenticationManager = this.context
            .getBean(AuthenticationManager.class);
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken("user002", "user"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        recipeService.findAllIsVisibilityAndLikeName("recipe002", new PageRequest(0, 10));
    }


}
