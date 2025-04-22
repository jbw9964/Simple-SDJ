package group.practice.services.dto.request;

import jakarta.validation.constraints.*;

public record RemoveMemberFromTeamReq(
        @NotNull
        @Min(1)
        Long teamId,

        @NotNull
        @Min(1)
        Long memberId
) {

}
