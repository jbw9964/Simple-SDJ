package group.practice.exceptions.service;

import group.practice.exceptions.*;

public class TeamNotFound extends CustomException {

    private TeamNotFound() {
        super(ExceptionStatus.TEAM_NOT_FOUND);
    }

    public static TeamNotFound of() {
        return new TeamNotFound();
    }
}
