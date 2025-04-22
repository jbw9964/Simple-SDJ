package group.practice.services.dto.request;

import jakarta.validation.constraints.*;

public record RemoveTeamReq(
        @NotNull
        @Min(1)
        Long teamId
) {

}
