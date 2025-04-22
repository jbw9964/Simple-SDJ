package group.practice.services;

import group.practice.entities.*;
import group.practice.services.dto.response.*;

public abstract class ConvertUtils {

    public static ShowMemberInfoResp EntityToResp(Member member) {
        MemberInfo info = member.getInfo();

        return info == null ?
                ShowMemberInfoResp.of(member) :
                ShowMemberInfoResp.of(member, info);
    }

    public static ShowTeamInfoResp EntityToResp(Team team) {
        TeamInfo teamInfo = team.getTeamInfo();
        Member teamOwner = team.getTeamOwner();

        ShowMemberInfoResp ownerInfoResp = teamOwner != null ?
                EntityToResp(teamOwner) : null;

        return teamInfo == null ?
                ShowTeamInfoResp.of(team, ownerInfoResp) :
                ShowTeamInfoResp.of(team, ownerInfoResp, teamInfo);
    }
}
