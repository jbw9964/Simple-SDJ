package group.practice.services;

import group.practice.services.dto.response.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;

public interface ViewService {

    ShowMemberInfoResp showMemberInfo(Long memberId);

    ShowTeamInfoResp showTeamInfo(Long teamId);

    PagedModel<ShowMemberInfoResp> showMemberInfos(Pageable pageable);

    PagedModel<ShowTeamInfoResp> showTeamInfos(Pageable pageable);

    PagedModel<ShowMemberInfoResp> showMembersInTeam(Long teamId, Pageable pageable);

    PagedModel<ShowTeamInfoResp> showTeamsOnMember(Long memberId, Pageable pageable);
}
