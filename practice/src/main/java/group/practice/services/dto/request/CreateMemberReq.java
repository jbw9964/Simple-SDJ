package group.practice.services.dto.request;

import jakarta.validation.constraints.*;

public record CreateMemberReq(
        BaseInfoReq baseInfo,
        String phone,
        String comment,

        @NotNull
        @Min(1)
        @Max(150)
        Integer age
) {

}
