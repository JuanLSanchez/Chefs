package es.juanlsanchez.chefs.web.rest.dto;

import es.juanlsanchez.chefs.domain.Authority;
import es.juanlsanchez.chefs.domain.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing information of requests.
 */
public class RequestInfoDTO {

    private Long nFollowers;
    private Long nFollowed;
    private Long nWaiting;

    public RequestInfoDTO(Long nFollowers, Long nFollowed, Long nWaiting) {
        this.nFollowers = nFollowers;
        this.nFollowed = nFollowed;
        this.nWaiting = nWaiting;
    }

    public Long getnFollowers() {
        return nFollowers;
    }

    public Long getnFollowed() {
        return nFollowed;
    }

    public Long getnWaiting() {
        return nWaiting;
    }

    @Override
    public String toString() {
        return "RequestInfoDTO{" +
            "nFollowers=" + nFollowers +
            ", nFollowed=" + nFollowed +
            ", nWaiting=" + nWaiting +
            '}';
    }
}
