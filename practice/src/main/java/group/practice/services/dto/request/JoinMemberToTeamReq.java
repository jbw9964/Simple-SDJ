package group.practice.services.dto.request;

import jakarta.validation.constraints.*;

public record JoinMemberToTeamReq(
        @NotNull
        @Min(1)
        Long memberId,

        @NotNull
        @Min(1)
        Long teamId
) {

}
