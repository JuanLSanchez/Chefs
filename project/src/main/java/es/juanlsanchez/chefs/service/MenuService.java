package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Menu;
import es.juanlsanchez.chefs.repository.MenuRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.service.util.ErrorMessageService;
import es.juanlsanchez.chefs.web.rest.dto.MenuDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * chefs
 * Created by juanlu on 22-mar-2016.
 */
@Service
@Transactional
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private ScheduleService scheduleService;

    /* CRUD */
    public MenuDTO create(Menu menu, Long scheduleId) {
        MenuDTO result;

        checkSchedule(scheduleId);
        menu.setSchedule(scheduleService.findOne(scheduleId).get());
        result = new MenuDTO(menuRepository.save(menu));

        return result;
    }

    public MenuDTO update(Menu menu, Long scheduleId) {
        MenuDTO result;

        checkSchedule(scheduleId);
        result = new MenuDTO(menuRepository.save(menu));

        return result;
    }

    public List<MenuDTO> findAllByScheduleId(Long scheduleId) {
        checkSchedule(scheduleId);
        return menuRepository.findAllByScheduleId(scheduleId)
            .stream().map(MenuDTO::new)
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        Optional.ofNullable(menuRepository.findOne(id))
            .filter(menu -> menu.getSchedule().getUser().getLogin().equals(SecurityUtils.getCurrentLogin()))
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_MENU));
        menuRepository.delete(id);
    }
    /* OTHERS */
    private void checkSchedule(Long scheduleId){

        scheduleService.findOne(scheduleId)
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_MENU));
    }
}
