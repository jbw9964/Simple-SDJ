package group.practice.services;

import group.practice.services.dto.request.*;

public interface JoinService {

    Long joinMemberToTeam(JoinMemberToTeamReq req);

    Long removeMemberFromTeam(RemoveMemberFromTeamReq req);
}
