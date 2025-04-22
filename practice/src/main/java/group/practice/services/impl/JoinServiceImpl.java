package group.practice.services.impl;

import group.practice.configs.aspects.logging.*;
import group.practice.entities.*;
import group.practice.exceptions.service.*;
import group.practice.repositories.*;
import group.practice.services.*;
import group.practice.services.dto.request.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@Transactional
@LogMethodIO
@LogException
@RequiredArgsConstructor
public class JoinServiceImpl implements JoinService {

    private final MemberRepository memberRepo;
    private final TeamRepository teamRepo;
    private final MemberTeamJoinRepository memberTeamJoinRepo;

    @Override
    public Long joinMemberToTeam(JoinMemberToTeamReq req) {

        Long memberId = req.memberId();
        Long teamId = req.teamId();

        assert memberId != null && teamId != null;

        // get member & team
        Member joinMember = memberRepo.findById(memberId)
                .orElseThrow(MemberNotFound::of);
        Team joinTeam = teamRepo.findById(teamId)
                .orElseThrow(TeamNotFound::of);

        // save new join & return id
        return memberTeamJoinRepo.save(
                MemberTeamJoin.of(joinMember, joinTeam)
        ).getId();
    }

    @Override
    public Long removeMemberFromTeam(
            RemoveMemberFromTeamReq req
    ) {

        Long memberId = req.memberId();
        Long teamId = req.teamId();

        assert memberId != null && teamId != null;

        // find & delete target
        MemberTeamJoin target = memberTeamJoinRepo
                .findByMemberIdAndTeamId(memberId, teamId)
                .orElseThrow(MemberTeamJoinNotFound::of);
        memberTeamJoinRepo.delete(target);

        return target.getId();
    }
}
