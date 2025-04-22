package group.practice.controllers;

import group.practice.configs.aspects.logging.*;
import group.practice.configs.aspects.response.SuccessResponse.*;
import group.practice.exceptions.controller.*;
import group.practice.services.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@LogMethodIO
@LogException
@RequestMapping("/view")
public class ViewController {

    private final ViewService viewService;

    @GetMapping("/member")
    public Code200 showMemberInfos(
            @RequestParam(
                    value = "pageNum", defaultValue = "0"
            ) Integer pageNum,
            @RequestParam(
                    value = "pageSize", defaultValue = "20"
            ) Integer pageSize,
            @RequestParam(
                    value = "idAcs", defaultValue = "true"
            ) Boolean idDesc
    ) {
        return Code200.of(viewService.showMemberInfos(
                toPageRequest(pageNum, pageSize, idDesc)
        ));
    }

    @GetMapping("/member/{id}")
    public Code200 showMemberInfo(
            @PathVariable("id") Long memberId
    ) {

        validateId(memberId);

        return Code200.of(viewService.showMemberInfo(memberId));
    }

    @GetMapping("/member/{id}/team")
    public Code200 showTeamsOnMember(
            @PathVariable("id") Long memberId,
            @RequestParam(
                    value = "pageNum", defaultValue = "0"
            ) Integer pageNum,
            @RequestParam(
                    value = "pageSize", defaultValue = "20"
            ) Integer pageSize,
            @RequestParam(
                    value = "idAcs", defaultValue = "true"
            ) Boolean idDesc
    ) {

        validateId(memberId);

        return Code200.of(viewService.showTeamsOnMember(
                memberId,
                toPageRequest(pageNum, pageSize, idDesc)
        ));
    }


    @GetMapping("/team")
    public Code200 showTeamInfo(
            @RequestParam(
                    value = "pageNum", defaultValue = "0"
            ) Integer pageNum,
            @RequestParam(
                    value = "pageSize", defaultValue = "20"
            ) Integer pageSize,
            @RequestParam(
                    value = "idAcs", defaultValue = "true"
            ) Boolean idDesc
    ) {
        return Code200.of(viewService.showTeamInfos(
                toPageRequest(pageNum, pageSize, idDesc)
        ));
    }

    @GetMapping("/team/{id}")
    public Code200 showTeamInfo(
            @PathVariable("id") Long teamId
    ) {

        validateId(teamId);

        return Code200.of(viewService.showTeamInfo(teamId));
    }

    @GetMapping("/team/{id}/member")
    public Code200 showMembersInTeam(
            @PathVariable("id") Long teamId,
            @RequestParam(
                    value = "pageNum", defaultValue = "0"
            ) Integer pageNum,
            @RequestParam(
                    value = "pageSize", defaultValue = "20"
            ) Integer pageSize,
            @RequestParam(
                    value = "idAcs", defaultValue = "true"
            ) Boolean idDesc
    ) {

        validateId(teamId);

        return Code200.of(viewService.showMembersInTeam(
                teamId, toPageRequest(pageNum, pageSize, idDesc)
        ));
    }

    private PageRequest toPageRequest(
            Integer pageNum, Integer pageSize, Boolean idDesc
    ) {

        validatePagingParams(pageNum, pageSize);

        return PageRequest.of(
                pageNum, pageSize, getSort(idDesc)
        );
    }

    private void validatePagingParams(int num, int size) {
        if (num < 0 || size <= 0) {
            throw InvalidPagingRequest.of();
        }
    }

    private Sort getSort(boolean idAcs) {
        return Sort.by(
                idAcs ? Direction.ASC : Direction.DESC,
                "id"
        );
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw InvalidIdRequest.of();
        }
    }
}

