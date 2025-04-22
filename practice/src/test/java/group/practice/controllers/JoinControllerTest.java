package group.practice.controllers;

import static group.practice.MvcTestUtils.UriToRequestBuilder.*;
import static group.practice.MvcTestUtils.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;

import group.practice.*;
import group.practice.configs.aspects.response.ErrorResponse.*;
import group.practice.configs.aspects.response.SuccessResponse.*;
import group.practice.exceptions.service.*;
import group.practice.services.*;
import group.practice.services.dto.request.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

@Slf4j
class JoinControllerTest extends ControllerTestSupport {

    static final Long validId = 1L;
    static final Long invalidId = Long.MAX_VALUE;

    JoinMemberToTeamReq validJoinReq
            = new JoinMemberToTeamReq(validId, validId);
    Long validJoinResp = validId;

    JoinMemberToTeamReq invalidJoinReq_mem
            = new JoinMemberToTeamReq(invalidId, validId);

    JoinMemberToTeamReq invalidJoinReq_team
            = new JoinMemberToTeamReq(validId, invalidId);

    RemoveMemberFromTeamReq validRemoveReq
            = new RemoveMemberFromTeamReq(validId, validId);
    Long validRemoveResp = validId;

    RemoveMemberFromTeamReq invalidRemoveReq_mem
            = new RemoveMemberFromTeamReq(invalidId, validId);

    RemoveMemberFromTeamReq invalidRemoveReq_team
            = new RemoveMemberFromTeamReq(validId, invalidId);

    private static Stream<Arguments> invalidJoinRequests() {
        BiFunction<Long, Long, JoinMemberToTeamReq> reqGen
                = JoinMemberToTeamReq::new;

        return createInvalidRequests(reqGen);
    }

    static Stream<Arguments> invalidRemoveRequests() {
        BiFunction<Long, Long, RemoveMemberFromTeamReq> reqGen
                = RemoveMemberFromTeamReq::new;

        return createInvalidRequests(reqGen);
    }

    static <T> Stream<Arguments> createInvalidRequests(
            BiFunction<Long, Long, T> reqGen
    ) {
        return Stream.of(
                Arguments.of(reqGen.apply(null, validId)),
                Arguments.of(reqGen.apply(validId, null)),
                Arguments.of(reqGen.apply(null, null)),
                Arguments.of(reqGen.apply(0L, validId)),
                Arguments.of(reqGen.apply(validId, 0L)),
                Arguments.of(reqGen.apply(0L, 0L))
        );
    }

    @Override
    @BeforeEach
    protected void setupMocks() {

        JoinService joinService
                = super.getJoinService();

        when(
                joinService.joinMemberToTeam(
                        any(JoinMemberToTeamReq.class)
                )
        ).thenAnswer(invocation -> {

            // record switch 는 jdk 21 부터 가능하다고 함. 아쉽네
            var req = invocation.getArgument(0,
                    JoinMemberToTeamReq.class);

            if (req.equals(validJoinReq)) {
                return validJoinResp;
            }

            if (req.equals(invalidJoinReq_mem)) {
                throw MemberNotFound.of();
            }

            if (req.equals(invalidJoinReq_team)) {
                throw TeamNotFound.of();
            }

            throw new RuntimeException("예상치 못한 arg");
        });

        when(joinService.removeMemberFromTeam(
                any(RemoveMemberFromTeamReq.class)
        )).thenAnswer(invocation -> {
            var req = invocation.getArgument(0,
                    RemoveMemberFromTeamReq.class);

            if (req.equals(validRemoveReq)) {
                return validRemoveResp;
            }

            if (req.equals(invalidRemoveReq_mem) ||
                req.equals(invalidRemoveReq_team)) {
                throw MemberTeamJoinNotFound.of();
            }

            throw new RuntimeException("예상치 못한 arg");
        });
    }

    @Test
    @DisplayName("새로운 팀에 참석하는 응답 확인")
    void joinMemberToTeam1() throws Exception {

        final String requestUri = "/team.join";

        // 정상 응답 확인
        var expectedResp = Code200.of(validJoinResp)
                .setRequestUri(requestUri);
        super.assertResponse(
                POST, requestUri, validJoinReq, expectedResp
        );

        // 서비스 에러 응답 확인
        // [1] : MemberNotFound
        var memNotFoundResp = buildResponseFromEx(
                requestUri, MemberNotFound.of()
        );
        super.assertResponse(
                POST, requestUri, invalidJoinReq_mem,
                APPLICATION_JSON, memNotFoundResp,
                true, true, false, false
        );

        // [2] : TeamNotFound
        var teamNotFoundResp = buildResponseFromEx(
                requestUri, TeamNotFound.of()
        );
        super.assertResponse(
                POST, requestUri, invalidJoinReq_team,
                APPLICATION_JSON, teamNotFoundResp,
                true, true, false, false
        );
    }

    @ParameterizedTest
    @MethodSource("invalidJoinRequests")
    @DisplayName("새로운 팀에 참석하는 요청 validation 확인")
    void joinMemberToTeam2(JoinMemberToTeamReq invalidReq)
            throws Exception {

        final String requestUri = "/team.join";

        var errResponse = Code400.of(null)
                .setRequestUri(requestUri);
        super.assertResponse(
                POST, requestUri, invalidReq,
                APPLICATION_JSON, errResponse,
                false, true,
                false, false
        );
    }

    @Test
    @DisplayName("팀에서 제외되는 요청 응답 확인")
    void removeMemberFromTeam1() throws Exception {

        final String requestUri = "/team.leave";

        // 정상 응답 확인
        var expectedResp = Code200.of(validRemoveResp)
                .setRequestUri(requestUri);
        super.assertResponse(
                DELETE, requestUri, validRemoveReq, expectedResp
        );

        // 서비스 에러 응답 확인
        var memberTeamNotFoundResp = buildResponseFromEx(
                requestUri, MemberTeamJoinNotFound.of()
        );
        super.assertResponse(
                DELETE, requestUri, invalidRemoveReq_mem,
                APPLICATION_JSON, memberTeamNotFoundResp,
                true, true,
                false, false
        );
        super.assertResponse(
                DELETE, requestUri, invalidRemoveReq_team,
                APPLICATION_JSON, memberTeamNotFoundResp,
                true, true,
                false, false
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRemoveRequests")
    @DisplayName("팀에서 제외되는 요청 validation 확인")
    void removeMemberFromTeam2(RemoveMemberFromTeamReq invalidReq)
            throws Exception {

        final String requestUri = "/team.leave";

        var errResponse = Code400.of(null)
                .setRequestUri(requestUri);
        super.assertResponse(
                DELETE, requestUri, invalidReq,
                APPLICATION_JSON, errResponse,
                false, true,
                false, false
        );
    }
}