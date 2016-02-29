package es.juanlsanchez.chefs.web.rest.dto;

/**
 * A DTO representing information of requests.
 */
public class RequestInfoDTO {

    private Long nFollowers;
    private Long nFollowed;
    private Long nRecipes;
    private Long nWaiting;

    public RequestInfoDTO(Long nFollowers, Long nFollowed, Long nWaiting, Long nRecipes) {
        this.nFollowers = nFollowers;
        this.nFollowed = nFollowed;
        this.nWaiting = nWaiting;
        this.nRecipes = nRecipes;
    }

    public Long getnFollowers() { return nFollowers; }

    public Long getnFollowed() {
        return nFollowed;
    }

    public Long getnWaiting() {
        return nWaiting;
    }

    public Long getnRecipes() { return nRecipes; }

    @Override
    public String toString() {
        return "RequestInfoDTO{" +
            "nFollowers=" + nFollowers +
            ", nFollowed=" + nFollowed +
            ", nRecipes=" + nRecipes +
            ", nWaiting=" + nWaiting +
            '}';
    }

}
