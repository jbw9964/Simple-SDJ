package group.practice.services.dto.request;

import jakarta.validation.constraints.*;

public record RemoveMemberReq(
        @NotNull
        @Min(1)
        Long yourMemberId
) {

}
