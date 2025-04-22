package group.practice.services.dto.response;

import group.practice.entities.*;

public record CreateTeamResp(
        Long teamId,
        Long ownerId,
        BaseInfoResp baseInfo,
        String description,
        String representation
) {

    public static CreateTeamResp of(Team team, Member owner, TeamInfo info) {
        return new CreateTeamResp(
                team.getId(), owner.getId(), BaseInfoResp.of(info),
                info.getDescription(), info.getRepresentation()
        );
    }
}
