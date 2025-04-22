package group.practice.services.dto.response;

import group.practice.entities.*;

public record CreateMemberResp(
        Long memberId,
        BaseInfoResp baseInfo,
        String phone,
        String comment,
        Integer age
) {

    public static CreateMemberResp of(Member member, MemberInfo info) {
        return new CreateMemberResp(
                member.getId(), BaseInfoResp.of(info),
                info.getPhone(), info.getComment(), info.getAge()
        );
    }

}
