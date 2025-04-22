package group.practice.services;

import static group.practice.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import group.practice.*;
import group.practice.exceptions.service.*;
import group.practice.repositories.*;
import group.practice.services.dto.request.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.transaction.annotation.*;

@Slf4j
class JoinServiceTest extends IntegrationTestSupport {

    @Autowired
    MemberTeamJoinRepository memberTeamJoinRepo;

    @Autowired
    JoinService joinService;

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
    @DisplayName("팀에 새로운 멤버를 추가한다.")
    void joinMemberToTeam() {

        Long memberId = getExistingMember().getId();
        Long teamId = getExistingTeam().getId();

        // create request
        var req = new JoinMemberToTeamReq(memberId, teamId);

        Long joinId = joinService.joinMemberToTeam(req);

        // validate response
        assertThat(joinId).isNotNull();

        // check real db
        assertExist(joinId, memberTeamJoinRepo::existsById);

        // check exceptions
        assertThrows(
                MemberNotFound.class,
                joinService::joinMemberToTeam,
                new JoinMemberToTeamReq(Long.MAX_VALUE, teamId)
        );
        assertThrows(
                TeamNotFound.class,
                joinService::joinMemberToTeam,
                new JoinMemberToTeamReq(memberId, Long.MAX_VALUE)
        );
    }

    @Test
    @DisplayName("팀에 멤버를 제거한다.")
    void removeMemberFromTeam() {
        Long memberId = getExistingMember().getId();
        Long teamId = getExistingTeam().getId();

        // create request
        var req = new RemoveMemberFromTeamReq(memberId, teamId);

        Long removedId = joinService.removeMemberFromTeam(req);

        // validate response
        assertThat(removedId).isNotNull();

        // check real db
        assertNotExist(removedId, memberTeamJoinRepo::existsById);

        // check exceptions
        assertThrows(
                MemberTeamJoinNotFound.class,
                joinService::removeMemberFromTeam,
                new RemoveMemberFromTeamReq(Long.MAX_VALUE, teamId),
                new RemoveMemberFromTeamReq(memberId, Long.MAX_VALUE)
        );
    }
}