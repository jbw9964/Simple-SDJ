package group.practice.controllers;

import static group.practice.MvcTestUtils.*;
import static group.practice.MvcTestUtils.UriToRequestBuilder.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;

import group.practice.*;
import group.practice.configs.aspects.response.ErrorResponse.*;
import group.practice.configs.aspects.response.SuccessResponse.*;
import group.practice.exceptions.service.*;
import group.practice.services.*;
import group.practice.services.dto.request.*;
import group.practice.services.dto.response.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.invocation.*;

@Slf4j
class RegisterControllerTest extends ControllerTestSupport {

    static final Long validId = 1L;
    static final Long invalidId = Long.MAX_VALUE;

    BaseInfoReq baseInfoReq
            = new BaseInfoReq("name", "email");
    BaseInfoResp baseInfoResp
            = new BaseInfoResp(validId, "name", "email");

    Request validCreateMem = Request.of(
            new CreateMemberReq(baseInfoReq, "phone",
                    "comment", 25),
            new CreateMemberResp(validId, baseInfoResp, "phone",
                    "comment", 25)
    );

    Request validCreateTeam = Request.of(
            new CreateTeamReq(baseInfoReq, "description",
                    "representation", validId),
            new CreateTeamResp(validId, validId, baseInfoResp,
                    "description", "representation")
    );

    Request invalidCreateTeam = Request.of(
            new CreateTeamReq(baseInfoReq, null,
                    null, invalidId),
            MemberNotFound.of()
    );

    Request validRemoveMem = Request.of(
            new RemoveMemberReq(validId), validId
    );

    Request invalidRemoveMem = Request.of(
            new RemoveMemberReq(invalidId), MemberNotFound.of()
    );

    Request validRemoveTeam = Request.of(
            new RemoveTeamReq(validId), validId
    );
    final List<Request> validRequests = Arrays.asList(
            validCreateMem, validCreateTeam,
            validRemoveMem, validRemoveTeam
    );
    Request invalidRemoveTeam = Request.of(
            new RemoveTeamReq(invalidId), TeamNotFound.of()
    );
    final List<Request> invalidRequests = Arrays.asList(
            invalidCreateTeam, invalidRemoveTeam,
            invalidRemoveMem
    );

    @SuppressWarnings("DataFlowIssue")
    private static Stream<Arguments> invalidCreateMemRequests() {
        return Stream.of(
                Arguments.of(new CreateMemberReq(null, null, null, null)),
                Arguments.of(new CreateMemberReq(null, null, null, 200)),
                Arguments.of(new CreateMemberReq(null, null, null, 0))
        );
    }

    @SuppressWarnings("DataFlowIssue")
    private static Stream<Arguments> invalidCreateTeamRequests() {
        return Stream.of(
                Arguments.of(new CreateTeamReq(null, null, null, null)),
                Arguments.of(new CreateTeamReq(null, null, null, 0L))
        );
    }

    private static Stream<Arguments> invalidRemoveMemberRequests() {
        Function<Long, RemoveMemberReq> reqGen = RemoveMemberReq::new;

        return Stream.of(
                Arguments.of(reqGen.apply(null)),
                Arguments.of(reqGen.apply(0L))
        );
    }

    private static Stream<Arguments> invalidRemoveTeamRequests() {
        Function<Long, RemoveTeamReq> reqGen = RemoveTeamReq::new;

        return Stream.of(
                Arguments.of(reqGen.apply(null)),
                Arguments.of(reqGen.apply(0L))
        );
    }

    @Override
    @BeforeEach
    protected void setupMocks() {

        RegisterService registerService
                = super.getRegisterService();

        when(registerService.createMember(
                any(CreateMemberReq.class)
        )).thenAnswer(this::setInvocation);

        when(registerService.createTeam(
                any(CreateTeamReq.class)
        )).thenAnswer(this::setInvocation);

        when(registerService.removeMember(
                any(RemoveMemberReq.class)
        )).thenAnswer(this::setInvocation);

        when(registerService.removeTeam(
                any(RemoveTeamReq.class)
        )).thenAnswer(this::setInvocation);
    }

    private Object setInvocation(InvocationOnMock invocation) throws Exception {
        var req = invocation.getArgument(0);

        for (Request validReq : validRequests) {
            if (validReq.request().equals(req)) {
                return validReq.response();
            }
        }

        for (Request invalidReq : invalidRequests) {
            if (invalidReq.request().equals(req)) {
                throw invalidReq.exception();
            }
        }

        throw new RuntimeException("예상치 못한 arg");
    }

    @Test
    @DisplayName("새로운 멤버 생성 요청 응답 확인")
    void createMember1() throws Exception {

        final String requestUri = "/member.register";

        // 정상 응답 확인
        var expectedResp = Code201.of(validCreateMem.response())
                .setRequestUri(requestUri);
        super.assertResponse(
                POST, requestUri, validCreateMem.request(), expectedResp
        );

        // 서비스 에러 응답 없음
    }

    @ParameterizedTest
    @MethodSource("invalidCreateMemRequests")
    @DisplayName("새로운 멤버 생성 요청 validation 확인")
    void createMember2(CreateMemberReq invalidReq) throws Exception {

        final String requestUri = "/member.register";

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
    @DisplayName("새로운 팀 생성 요청 응답 확인")
    void createTeam1() throws Exception {

        final String requestUri = "/team.register";

        // 정상 응답 확인
        var expectedResp = Code201.of(validCreateTeam.response())
                .setRequestUri(requestUri);
        super.assertResponse(
                POST, requestUri, validCreateTeam.request(), expectedResp
        );

        // 서비스 에러 응답 확인
        var memberNotFoundResp = buildResponseFromEx(
                requestUri, MemberNotFound.of()
        );
        super.assertResponse(
                POST, requestUri, invalidCreateTeam.request(),
                APPLICATION_JSON, memberNotFoundResp,
                true, true,
                false, false
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCreateTeamRequests")
    @DisplayName("새로운 팀 생성 요청 validation 확인")
    void createTeam2(CreateTeamReq invalidReq) throws Exception {

        final String requestUri = "/team.register";

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
    @DisplayName("멤버 삭제 요청 응답 확인")
    void removeMember1() throws Exception {

        final String requestUri = "/member.delete";

        // 정상 응답 확인
        var expectedResp = Code200.of(validRemoveMem.response())
                .setRequestUri(requestUri);
        super.assertResponse(
                DELETE, requestUri, validRemoveMem.request(), expectedResp
        );

        // 서비스 에러 응답 확인
        var memberNotFoundResp = buildResponseFromEx(
                requestUri, MemberNotFound.of()
        );
        super.assertResponse(
                DELETE, requestUri, invalidRemoveMem.request(),
                APPLICATION_JSON, memberNotFoundResp,
                true, true,
                false, false
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRemoveMemberRequests")
    @DisplayName("멤버 삭제 요청 validation 확인")
    void removeMember2(RemoveMemberReq invalidReq) throws Exception {

        final String requestUri = "/member.delete";

        var errResponse = Code400.of(null)
                .setRequestUri(requestUri);
        super.assertResponse(
                DELETE, requestUri, invalidReq,
                APPLICATION_JSON, errResponse,
                false, true,
                false, false
        );
    }

    @Test
    @DisplayName("팀 삭제 요청 응답 확인")
    void removeTeam1() throws Exception {

        final String requestUri = "/team.delete";

        // 정상 응답 확인
        var expectedResp = Code200.of(validRemoveTeam.response())
                .setRequestUri(requestUri);
        super.assertResponse(
                DELETE, requestUri,
                validRemoveTeam.request(), expectedResp
        );

        // 서비스 에러 응답 확인
        var teamNotFoundResp = buildResponseFromEx(
                requestUri, TeamNotFound.of()
        );
        super.assertResponse(
                DELETE, requestUri,
                invalidRemoveTeam.request(),
                APPLICATION_JSON, teamNotFoundResp,
                true, true,
                false, false
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRemoveTeamRequests")
    @DisplayName("팀 삭제 요청 validation 확인")
    void removeTeam2(RemoveTeamReq invalidReq) throws Exception {
        final String requestUri = "/team.delete";

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