package group.practice.services.dto.response;

import group.practice.entities.base.*;

public record BaseInfoResp(
        Long infoId,
        String name,
        String email
) {

    public static BaseInfoResp of(Information information) {
        return new BaseInfoResp(
                information.getId(),
                information.getName(),
                information.getEmail()
        );
    }
}
