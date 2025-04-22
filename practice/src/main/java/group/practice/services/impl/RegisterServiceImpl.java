package group.practice.services.impl;

import group.practice.configs.aspects.logging.*;
import group.practice.entities.*;
import group.practice.exceptions.service.*;
import group.practice.repositories.*;
import group.practice.services.*;
import group.practice.services.dto.request.*;
import group.practice.services.dto.response.*;
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
public class RegisterServiceImpl implements RegisterService {

    private final MemberInfoRepository memberInfoRepo;
    private final MemberRepository memberRepo;
    private final TeamInfoRepository teamInfoRepo;
    private final TeamRepository teamRepo;
    private final MemberTeamJoinRepository memberTeamJoinRepo;

    @Override
    public CreateMemberResp createMember(CreateMemberReq req) {

        // create info
        MemberInfo info = memberInfoRepo.save(
                MemberInfo.getNew().setInfo(req)
        );

        // create member
        Member newMember = memberRepo.save(
                Member.getNew().setInfo(info)
        );

        return CreateMemberResp.of(newMember, info);
    }

    @Override
    public CreateTeamResp createTeam(CreateTeamReq req) {

        // find owner
        Long ownerId = req.ownerId();
        Member owner = memberRepo.findById(ownerId)
                .orElseThrow(MemberNotFound::of);

        // create info
        TeamInfo teamInfo = teamInfoRepo.save(
                TeamInfo.getNew().setInfo(req)
        );

        // create team
        Team newTeam = teamRepo.save(
                Team.getNew().setTeamOwner(owner).setTeamInfo(teamInfo)
        );

        // create join record
        memberTeamJoinRepo.save(
                MemberTeamJoin.of(owner, newTeam)
        );

        return CreateTeamResp.of(newTeam, owner, teamInfo);
    }

    @Override
    public Long removeMember(RemoveMemberReq req) {

        Long memberId = req.yourMemberId();

        // remove all join records
        memberTeamJoinRepo.deleteAllByJoinedMemberId(memberId);

        // change team owners as null within target
        teamRepo.updateOwnerAsNullOnMemberId(memberId);

        // find & delete target
        Member target = memberRepo.findById(req.yourMemberId())
                .orElseThrow(MemberNotFound::of);
        memberRepo.delete(target);

        return target.getId();
    }

    @Override
    public Long removeTeam(RemoveTeamReq req) {

        Long teamId = req.teamId();

        // remove all join records
        memberTeamJoinRepo.deleteAllByJoinedTeamId(teamId);

        // find & delete target
        Team target = teamRepo.findById(teamId)
                .orElseThrow(TeamNotFound::of);
        teamRepo.delete(target);

        return target.getId();
    }
}
