package group.practice.services;

import static group.practice.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import group.practice.*;
import group.practice.entities.*;
import group.practice.exceptions.service.*;
import group.practice.repositories.*;
import group.practice.services.dto.request.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.transaction.annotation.*;

@Slf4j
class RegisterServiceTest extends IntegrationTestSupport {

    @Autowired
    MemberRepository memberRepo;

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    MemberInfoRepository memberInfoRepo;

    @Autowired
    TeamInfoRepository teamInfoRepo;

    @Autowired
    MemberTeamJoinRepository memberTeamJoinRepo;

    @Autowired
    RegisterService registerService;

    @Override
    @BeforeEach
    @Transactional
    protected void setupData() {
        super.setBasicData();
    }

    @Override
    @AfterEach
    protected void clearData() {
        super.removeAllData();
    }

    @Test
    @DisplayName("새로운 멤버를 생성한다.")
    void register() {

        // create request
        var baseInfoReq = new BaseInfoReq("name", "email");
        var req = new CreateMemberReq(
                baseInfoReq, "phone", "comment", 20
        );

        // do
        var resp = registerService.createMember(req);

        Long memberId, infoId;

        memberId = resp.memberId();

        // validate response
        assertThat(memberId).isNotNull();
        assertThat(resp.phone()).isEqualTo(req.phone());
        assertThat(resp.comment()).isEqualTo(req.comment());
        assertThat(resp.age()).isEqualTo(req.age());

        var baseInfoResp = resp.baseInfo();

        infoId = baseInfoResp.infoId();

        assertThat(infoId).isNotNull();
        assertThat(baseInfoResp.name()).isEqualTo(baseInfoReq.name());
        assertThat(baseInfoResp.email()).isEqualTo(baseInfoReq.email());

        // check real db
        assertExist(memberId, memberRepo::existsById);
        assertExist(infoId, memberInfoRepo::existsById);
    }


    @Test
    @DisplayName("새로운 팀을 생성한다.")
    void createTeam() {

        Long memberId = getExistingMember().getId();

        // create request
        var baseInfoReq = new BaseInfoReq("teamName", "teamEmail");
        var req = new CreateTeamReq(
                baseInfoReq, "description", "representation", memberId
        );

        // do
        var resp = registerService.createTeam(req);

        Long teamId, ownerId, infoId;

        teamId = resp.teamId();
        ownerId = resp.ownerId();

        // validate response
        assertThat(teamId).isNotNull();
        assertThat(ownerId).isEqualTo(memberId);
        assertThat(resp.description()).isEqualTo(req.description());
        assertThat(resp.representation()).isEqualTo(req.representation());

        var baseInfoResp = resp.baseInfo();

        infoId = baseInfoResp.infoId();

        assertThat(infoId).isNotNull();
        assertThat(baseInfoResp.name()).isEqualTo(baseInfoReq.name());
        assertThat(baseInfoResp.email()).isEqualTo(baseInfoReq.email());

        // check real db
        assertExist(teamId, teamRepo::existsById);
        assertExist(infoId, teamInfoRepo::existsById);
        assertThat(memberTeamJoinRepo.existsByMemberTeamId(
                memberId, teamId
        )).isTrue();

        // check exceptions
        Long randomMemberId = Long.MAX_VALUE;
        var noMemberExistReq = new CreateTeamReq(
                null, null, null,
                randomMemberId
        );

        assertThrows(
                MemberNotFound.class,
                registerService::createTeam,
                noMemberExistReq
        );
    }

    @Test
    @DisplayName("멤버를 삭제한다.")
    void removeMember() {

        Long memberId = getExistingMember().getId();
        Long memberInfoId = getExistingMemberInfo().getId();
        Long teamId = getExistingTeam().getId();
        Long memberTeamJoinId = getExistingMemberTeamJoin().getId();

        // create request
        var req = new RemoveMemberReq(memberId);

        Long removedId = registerService.removeMember(req);

        // validate response
        assertThat(removedId).isEqualTo(memberId);

        // check real db
        assertNotExist(memberId, memberRepo::existsById);
        assertNotExist(memberInfoId, memberInfoRepo::existsById);
        assertNotExist(memberTeamJoinId,
                memberTeamJoinRepo::existsById);

        Member teamOwner = teamRepo.findByIdFetchOwner(teamId)
                .orElseThrow().getTeamOwner();
        assertThat(teamOwner).isNull();

        // check exceptions
        assertThrows(
                MemberNotFound.class,
                registerService::removeMember,
                new RemoveMemberReq(Long.MAX_VALUE)
        );
    }

    @Test
    @DisplayName("팀을 삭제한다.")
    void removeTeam() {
        Long teamId = getExistingTeam().getId();
        Long teamInfoId = getExistingTeamInfo().getId();

        // create request

        var req = new RemoveTeamReq(teamId);

        Long removedId = registerService.removeTeam(req);

        // validate response
        assertThat(removedId).isEqualTo(teamId);

        // check real db
        assertNotExist(teamId, teamRepo::existsById);
        assertNotExist(teamInfoId, teamInfoRepo::existsById);
        assertNotExist(teamId, memberTeamJoinRepo::existsByTeamId);

        // check exceptions
        assertThrows(
                TeamNotFound.class,
                registerService::removeTeam,
                new RemoveTeamReq(Long.MAX_VALUE)
        );
    }
}