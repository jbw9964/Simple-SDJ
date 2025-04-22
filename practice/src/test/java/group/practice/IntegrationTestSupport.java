package group.practice;

import group.practice.entities.*;
import group.practice.repositories.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

@Slf4j
@SpringBootTest
public abstract class IntegrationTestSupport {

    // inject every bean you need in subclass.
    // since there's only one spring container on, every bean will be the same.

    protected @Getter Member existingMember;
    protected @Getter MemberInfo existingMemberInfo;
    protected @Getter Team existingTeam;
    protected @Getter TeamInfo existingTeamInfo;
    protected @Getter MemberTeamJoin existingMemberTeamJoin;
    // repository beans to provide default setup/clear data
    @Autowired
    MemberRepository memberRepo;
    @Autowired
    TeamRepository teamRepo;
    @Autowired
    MemberInfoRepository memberInfoRepo;
    @Autowired
    TeamInfoRepository teamInfoRepo;
    @Autowired
    MemberTeamJoinRepository memberTeamJoinRepo;

    protected abstract void setupData();

    protected abstract void clearData();

    protected void setBasicData() {
        log.info("Setting up basic data");
        existingMemberInfo = memberInfoRepo.save(
                MemberInfo.builder()
                        .phone("Member-phone")
                        .email("Member-email")
                        .name("Member-name")
                        .comment("Member-comment")
                        .age(30)
                        .build()
        );

        existingMember = memberRepo.save(
                Member.getNew().setInfo(existingMemberInfo)
        );

        existingTeamInfo = teamInfoRepo.save(
                TeamInfo.builder()
                        .name("Team-name")
                        .email("Team-email")
                        .description("Team-description")
                        .representation("Team-representation")
                        .build()
        );

        existingTeam = teamRepo.save(
                Team.getNew().setTeamInfo(existingTeamInfo)
                        .setTeamOwner(existingMember)
        );

        existingMemberTeamJoin = memberTeamJoinRepo.save(
                MemberTeamJoin.of(existingMember, existingTeam)
        );

        log.info("Basic data has been set");
    }

    protected void removeAllData() {
        log.info("Removing all data");
        memberTeamJoinRepo.deleteAll();
        teamRepo.deleteAll();
        memberRepo.deleteAll();
        teamInfoRepo.deleteAll();
        memberInfoRepo.deleteAll();
        log.info("All data has been removed");

        existingMember = null;
        existingMemberInfo = null;
        existingTeam = null;
        existingTeamInfo = null;
        existingMemberTeamJoin = null;
        log.info("Initialized existing entity as null.");
    }
}
