package group.practice.services.dto.response;

import group.practice.entities.*;

public record ShowTeamInfoResp(
        Long id,
        ShowMemberInfoResp teamOwnerInfo,
        BaseInfoResp baseInfo,
        String description,
        String representation
) {

    public static ShowTeamInfoResp of(
            Team team, ShowMemberInfoResp ownerInfo
    ) {
        return new ShowTeamInfoResp(
                team.getId(), ownerInfo,
                null, null, null
        );
    }

    public static ShowTeamInfoResp of(
            Team team, ShowMemberInfoResp ownerInfo,
            TeamInfo teamInfo
    ) {
        return new ShowTeamInfoResp(
                team.getId(), ownerInfo,
                BaseInfoResp.of(teamInfo),
                teamInfo.getDescription(),
                teamInfo.getRepresentation()
        );
    }
}
