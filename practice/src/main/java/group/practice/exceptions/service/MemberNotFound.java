package group.practice.exceptions.service;

import group.practice.exceptions.*;

public class MemberNotFound extends CustomException {

    private MemberNotFound() {
        super(ExceptionStatus.MEMBER_NOT_FOUND);
    }

    public static MemberNotFound of() {
        return new MemberNotFound();
    }
}
