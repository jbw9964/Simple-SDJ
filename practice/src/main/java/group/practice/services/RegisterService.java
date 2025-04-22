package group.practice.services;

import group.practice.services.dto.request.*;
import group.practice.services.dto.response.*;

public interface RegisterService {

    CreateMemberResp createMember(CreateMemberReq req);

    CreateTeamResp createTeam(CreateTeamReq req);

    Long removeMember(RemoveMemberReq req);

    Long removeTeam(RemoveTeamReq req);
}
