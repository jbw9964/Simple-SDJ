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
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping("/member.register")
    public Code201 createMember(
            @Valid @RequestBody CreateMemberReq req
    ) {
        return Code201.of(registerService.createMember(req));
    }

    @PostMapping("/team.register")
    public Code201 createTeam(
            @Valid @RequestBody CreateTeamReq req
    ) {
        return Code201.of(registerService.createTeam(req));
    }

    @DeleteMapping("/member.delete")
    public Code200 removeMember(
            @Valid @RequestBody RemoveMemberReq req
    ) {
        return Code200.of(registerService.removeMember(req));
    }

    @DeleteMapping("/team.delete")
    public Code200 removeTeam(
            @Valid @RequestBody RemoveTeamReq req
    ) {
        return Code200.of(registerService.removeTeam(req));
    }
}
