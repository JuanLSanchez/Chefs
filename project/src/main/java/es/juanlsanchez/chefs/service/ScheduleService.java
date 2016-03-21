package es.juanlsanchez.chefs.service;

import com.google.common.collect.Sets;
import es.juanlsanchez.chefs.domain.Schedule;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.ScheduleRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.service.util.ErrorMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * chefs
 * Created by juanlu on 21-mar-2016.
 */
@Service
@Transactional
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserService userService;

    /* Simple CRUD */
    public Schedule create(Schedule schedule) {
        Schedule result;
        User principal;

        principal = userService.getPrincipal();

        Assert.notNull(principal, ErrorMessageService.PRINCIPAL_IS_REQUIRED);

        schedule.setUser(principal);
        schedule.setMenus(Sets.newHashSet());

        result = scheduleRepository.save(schedule);

        return result;
    }

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    public Page<Schedule> findAllByUserIsCurrentUser(Pageable pageable) {
        Page<Schedule> result;
        Boolean isAuthenticated;
        try{
            isAuthenticated = SecurityUtils.isAuthenticated();
        }catch (Throwable e){
            isAuthenticated = false;
        }

        Assert.isTrue(isAuthenticated, ErrorMessageService.PRINCIPAL_IS_REQUIRED);
        result = scheduleRepository.findByUserIsCurrentUser(pageable);

        return result;
    }

    public Schedule update(Schedule schedule) {
        Schedule result;

        findOne(schedule.getId())
            .orElseThrow(() -> new IllegalArgumentException("The principal not contain this schedule"));

        result = scheduleRepository.save(schedule);

        return result;
    }

    public Optional<Schedule> findOne(Long id) {
        Optional<Schedule> result;
        String principal;

        principal = SecurityUtils.getCurrentLogin();

        result =  Optional.ofNullable(scheduleRepository.findOne(id))
            .filter(s -> s.getUser().getLogin().equals(principal));

        return result;
    }

    public void delete(Long id) {
        findOne(id)
            .orElseThrow(() -> new IllegalArgumentException("The principal not contain this schedule"));

        scheduleRepository.delete(id);

    }

    /* Others */
}
