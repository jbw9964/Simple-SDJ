package group.practice.exceptions.service;

import group.practice.exceptions.*;

public class MemberTeamJoinNotFound extends CustomException {

    private MemberTeamJoinNotFound() {
        super(ExceptionStatus.MEMBER_TEAM_JOIN_NOT_FOUND);
    }

    public static MemberTeamJoinNotFound of() {
        return new MemberTeamJoinNotFound();
    }
}
