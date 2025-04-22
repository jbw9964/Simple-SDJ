package group.practice.services.dto.request;

import jakarta.validation.constraints.*;

public record CreateTeamReq(
        BaseInfoReq baseInfo,
        String description,
        String representation,

        @NotNull
        @Min(1)
        Long ownerId
) {

}
