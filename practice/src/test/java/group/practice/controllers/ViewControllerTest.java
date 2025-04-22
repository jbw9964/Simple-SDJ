package group.practice.controllers;

import static group.practice.MvcTestUtils.UriToRequestBuilder.*;
import static org.mockito.Mockito.*;

import group.practice.*;
import group.practice.configs.aspects.response.SuccessResponse.*;
import group.practice.exceptions.service.*;
import group.practice.services.*;
import group.practice.services.dto.response.*;
import java.util.*;
import java.util.stream.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

// Pagination 테스트 코드는 어떻게 만들지 모르겠네...
// 아니 뭐 만들면 만들긴 하겠지만 테코가 엄청 더러울 것 같은데...
// 그럼에도 불구하고 만들어 두는게 좋을까??
// 아니면 꼭 테스트 해봐야 좋을 기능만 해두는게 좋을까?? 진짜 모르겠네..
@Slf4j
class ViewControllerTest extends ControllerTestSupport {

    static final int TOTAL_ELEMENTS = 30;
    final List<ShowMemberInfoResp> memberInfoRespList
            = LongStream.rangeClosed(1, TOTAL_ELEMENTS)
            .mapToObj(ViewControllerTest::genMemberInfo)
            .toList();
    final List<ShowTeamInfoResp> teamInfoRespList
            = LongStream.rangeClosed(1, TOTAL_ELEMENTS)
            .mapToObj(ViewControllerTest::genTeamInfo)
            .toList();

    private static BaseInfoResp genBaseInfo(Long id) {
        return new BaseInfoResp(
                id, "name" + id, "email" + id
        );
    }

    private static ShowMemberInfoResp genMemberInfo(Long id) {
        return new ShowMemberInfoResp(
                id, genBaseInfo(id),
                "phone" + id,
                "comment" + id,
                25
        );
    }

    private static ShowTeamInfoResp genTeamInfo(Long id) {
        return new ShowTeamInfoResp(
                id, genMemberInfo(id), genBaseInfo(id),
                "description" + id,
                "representation" + id
        );
    }

    @Override
    @BeforeEach
    protected void setupMocks() {

        ViewService viewService = super.getViewService();

        when(viewService.showMemberInfo(Mockito.anyLong()))
                .thenAnswer(invocation -> {
                    Long id = invocation
                            .getArgument(0, Long.class);

                    var find = memberInfoRespList.stream()
                            .filter(r -> id.equals(r.id()))
                            .findFirst();

                    return find.orElseThrow(MemberNotFound::of);
                });

        when(viewService.showTeamInfo(Mockito.anyLong()))
                .thenAnswer(invocation -> {
                    Long id = invocation
                            .getArgument(0, Long.class);
                    var find = teamInfoRespList.stream()
                            .filter(r -> id.equals(r.id()))
                            .findFirst();

                    return find.orElseThrow(TeamNotFound::of);
                });
    }

    @Test
    @DisplayName("단일 멤버 정보 조회 응답 확인")
    void showMemberInfo() throws Exception {

        for (var expectedBody : memberInfoRespList) {
            final String requestUri
                    = "/view/member/" + expectedBody.id();

            var expectedResp = Code200.of(expectedBody)
                    .setRequestUri(requestUri);

            super.assertResponse(
                    GET, requestUri, null, expectedResp
            );
        }
    }


    @Test
    @DisplayName("단일 팀 정보 조회 응답 확인")
    void showTeamInfo() throws Exception {

        for (var expectedBody : teamInfoRespList) {
            final String requestUri
                    = "/view/team/" + expectedBody.id();

            var expectedResp = Code200.of(expectedBody)
                    .setRequestUri(requestUri);

            super.assertResponse(
                    GET, requestUri, null, expectedResp
            );
        }
    }

}