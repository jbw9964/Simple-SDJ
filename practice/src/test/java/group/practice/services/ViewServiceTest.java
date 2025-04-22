package group.practice.services;

import static group.practice.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import group.practice.*;
import group.practice.entities.*;
import group.practice.entities.base.*;
import group.practice.exceptions.service.*;
import group.practice.repositories.*;
import group.practice.services.dto.response.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.transaction.annotation.*;

@Slf4j
class ViewServiceTest extends IntegrationTestSupport {

    // 페이징 테스트 때문에 어쩔 수 없이 @Transactional 사용...
    private static final int
            NUM_OF_ENTITIES = 35;
    private static final int
            TEST_PAGE_SIZE = 9;
    @Autowired
    MemberRepository memberRepo;
    @Autowired
    TeamRepository teamRepo;
    @Autowired
    ViewService viewService;

    static void testPagingElements(
            Function<Pageable, PagedModel<?>> func
    ) {

        int totalElements = NUM_OF_ENTITIES;
        int pageSize = TEST_PAGE_SIZE;

        Queue<Integer> pageSizeQ = new ArrayDeque<>();
        while (totalElements - pageSize > 0) {
            totalElements -= pageSize;
            pageSizeQ.add(pageSize);
        }
        pageSizeQ.add(totalElements);
        totalElements = NUM_OF_ENTITIES;

        int totalPages = pageSizeQ.size();

        for (int pageNum = 0;
                pageNum < totalPages;
                pageNum++) {

            // create request
            var req = PageRequest.of(pageNum, pageSize);

            // do
            var resp = func.apply(req);
            int contentSize = resp.getContent().size();

            // validate response
            var metadata = resp.getMetadata();

            assertThat(pageSizeQ).isNotEmpty();
            assertThat(metadata).isNotNull();
            assertThat(metadata.number()).isEqualTo(pageNum);
            assertThat(metadata.size()).isEqualTo(pageSize);
            assertThat(contentSize).isEqualTo(pageSizeQ.poll());
            assertThat(metadata.totalElements())
                    .isEqualTo(totalElements);
            assertThat(metadata.totalPages())
                    .isEqualTo(totalPages);
        }
    }

    static void assertMemberInfoEquality(
            MemberInfo info, ShowMemberInfoResp resp
    ) {
        assertBaseInfoEquality(info, resp.baseInfo());

        assertThat(resp.phone()).isEqualTo(info.getPhone());
        assertThat(resp.comment()).isEqualTo(info.getComment());
        assertThat(resp.age()).isEqualTo(info.getAge());
    }

    static void assertTeamInfoEquality(
            TeamInfo info, ShowTeamInfoResp resp
    ) {
        assertBaseInfoEquality(info, resp.baseInfo());

        assertThat(resp.description())
                .isEqualTo(info.getDescription());
        assertThat(resp.representation())
                .isEqualTo(info.getRepresentation());
    }

    static void assertBaseInfoEquality(
            Information info, BaseInfoResp baseInfo
    ) {
        assertThat(baseInfo).isNotNull();
        assertThat(baseInfo.infoId()).isEqualTo(info.getId());
        assertThat(baseInfo.name()).isEqualTo(info.getName());
        assertThat(baseInfo.email()).isEqualTo(info.getEmail());
    }

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
    @DisplayName("한 멤버 정보를 조회한다.")
    void showMemberInfo() {

        Member mem = getExistingMember();
        MemberInfo info = getExistingMemberInfo();

        // do
        var resp = viewService.showMemberInfo(
                mem.getId()
        );

        // validate response
        assertThat(resp.id()).isEqualTo(mem.getId());
        assertMemberInfoEquality(info, resp);

        // check exceptions
        assertThrows(
                MemberNotFound.class,
                viewService::showMemberInfo,
                Long.MAX_VALUE
        );
    }

    @Test
    @DisplayName("한 팀 정보를 조회한다.")
    void showTeamInfo() {
        Team team = getExistingTeam();
        TeamInfo info = getExistingTeamInfo();
        Member teamOwner = getExistingMember();
        MemberInfo ownerInfo = getExistingMemberInfo();

        // do
        var resp = viewService.showTeamInfo(team.getId());

        // validate response
        assertThat(resp.id()).isEqualTo(team.getId());
        assertTeamInfoEquality(info, resp);

        var ownerInfoResp = resp.teamOwnerInfo();

        assertThat(ownerInfoResp.id()).isEqualTo(teamOwner.getId());
        assertMemberInfoEquality(ownerInfo, ownerInfoResp);

        // check exceptions
        assertThrows(
                TeamNotFound.class,
                viewService::showTeamInfo,
                Long.MAX_VALUE
        );
    }

    @Test
    @Transactional
    @DisplayName("멤버 정보를 페이징 조회한다.")
    void showMemberInfos() {

        super.removeAllData();

        IntStream.range(0, NUM_OF_ENTITIES)
                .mapToObj(i -> Member.getNew())
                .forEach(memberRepo::save);

        testPagingElements(viewService::showMemberInfos);
    }

    @Test
    @Transactional
    @DisplayName("팀 정보를 페이징 조회한다.")
    void showTeamInfos() {

        super.removeAllData();

        IntStream.range(0, NUM_OF_ENTITIES)
                .mapToObj(i -> Team.getNew())
                .forEach(teamRepo::save);

        testPagingElements(viewService::showTeamInfos);
    }
}