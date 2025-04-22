package group.practice.exceptions;

import lombok.*;

@Getter
public enum ExceptionStatus {
    MEMBER_NOT_FOUND(404, "멤버를 찾을 수 없습니다."),
    TEAM_NOT_FOUND(404, "팀을 찾을 수 없습니다."),
    MEMBER_TEAM_JOIN_NOT_FOUND(404, "멤버가 팀에 참여한 기록이 없습니다."),
    INVALID_PAGING_REQUEST(400, "페이징 요청 파라미터가 잘못되었습니다."),
    INVALID_ID_REQUEST(400, "ID 는 0 보다 커야합니다.");

    private final int statusCode;
    private final String message;

    ExceptionStatus(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
