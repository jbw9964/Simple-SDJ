package group.practice.controllers;

import group.practice.configs.aspects.logging.*;
import group.practice.configs.aspects.response.SuccessResponse.*;
import group.practice.services.*;
import group.practice.services.dto.request.*;
import jakarta.validation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@LogMethodIO
public class JoinController {

    private final JoinService joinService;

    @PostMapping("/team.join")
    public Code200 joinMemberToTeam(
            @Valid @RequestBody JoinMemberToTeamReq req
    ) {
        return Code200.of(joinService.joinMemberToTeam(req));
    }

    @DeleteMapping("/team.leave")
    public Code200 removeMemberFromTeam(
            @Valid @RequestBody RemoveMemberFromTeamReq req
    ) {
        return Code200.of(joinService.removeMemberFromTeam(req));
    }
}
