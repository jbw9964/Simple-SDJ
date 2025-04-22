package group.practice.services.dto.response;

import group.practice.entities.*;

public record ShowMemberInfoResp(
        Long id,
        BaseInfoResp baseInfo,
        String phone,
        String comment,
        Integer age
) {

    public static ShowMemberInfoResp of(Member member) {
        return new ShowMemberInfoResp(
                member.getId(),
                null, null,
                null, null
        );
    }

    public static ShowMemberInfoResp of(
            Member member, MemberInfo info
    ) {
        return new ShowMemberInfoResp(
                member.getId(), BaseInfoResp.of(info),
                info.getPhone(), info.getComment(),
                info.getAge()
        );
    }
}
