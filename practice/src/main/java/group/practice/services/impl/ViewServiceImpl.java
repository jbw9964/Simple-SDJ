package group.practice.services.impl;

import static group.practice.services.ConvertUtils.*;

import group.practice.configs.aspects.logging.*;
import group.practice.entities.*;
import group.practice.exceptions.service.*;
import group.practice.repositories.*;
import group.practice.services.*;
import group.practice.services.dto.response.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@LogMethodIO
@LogException
@RequiredArgsConstructor
public class ViewServiceImpl implements ViewService {

    private final MemberRepository memberRepo;
    private final TeamRepository teamRepo;
    private final MemberTeamJoinRepository memberTeamJoinRepo;

    @Override
    public ShowMemberInfoResp showMemberInfo(Long memberId) {

        // find with related info
        Member find = memberRepo.findByIdFetchInfo(memberId)
                .orElseThrow(MemberNotFound::of);

        // build response
        return EntityToResp(find);
    }

    @Override
    public ShowTeamInfoResp showTeamInfo(Long teamId) {

        // find with related info
        Team find = teamRepo.findByIdFetchInfoAndOwner(teamId)
                .orElseThrow(TeamNotFound::of);

        // build response
        return EntityToResp(find);
    }

    @Override
    public PagedModel<ShowMemberInfoResp> showMemberInfos(Pageable pageable) {

        // get paging result
        Page<Member> paged = memberRepo.findAllFetchInfo(pageable);

        // map content to response
        Page<ShowMemberInfoResp> mapped = paged
                .map(ConvertUtils::EntityToResp);

        // to page model
        return new PagedModel<>(mapped);
    }

    @Override
    public PagedModel<ShowTeamInfoResp> showTeamInfos(Pageable pageable) {

        // get paging result
        Page<Team> paged
                = teamRepo.findAllFetchInfoAndOwner(pageable);

        // map content to response
        Page<ShowTeamInfoResp> mapped = paged
                .map(ConvertUtils::EntityToResp);

        // to page model
        return new PagedModel<>(mapped);
    }

    @Override
    public PagedModel<ShowMemberInfoResp> showMembersInTeam(
            Long teamId, Pageable pageable
    ) {

        // get paging result
        Page<MemberTeamJoin> paged = memberTeamJoinRepo
                .findAllByTeamIdWithFetching(teamId, pageable);

        // map content to entity, response
        Page<ShowMemberInfoResp> mapped = paged
                .map(MemberTeamJoin::getJoinedMember)
                .map(ConvertUtils::EntityToResp);

        // to page model
        return new PagedModel<>(mapped);
    }

    @Override
    public PagedModel<ShowTeamInfoResp> showTeamsOnMember(
            Long memberId, Pageable pageable
    ) {

        // get paging result
        Page<MemberTeamJoin> paged = memberTeamJoinRepo
                .findAllByMemberIdWithFetching(memberId, pageable);

        // map content to entity, response
        Page<ShowTeamInfoResp> mapped = paged
                .map(MemberTeamJoin::getJoinedTeam)
                .map(ConvertUtils::EntityToResp);

        // to page model
        return new PagedModel<>(mapped);
    }
}
