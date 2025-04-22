package group.practice.repositories;

import group.practice.entities.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface MemberTeamJoinRepository
        extends JpaRepository<MemberTeamJoin, Long> {

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("delete from MemberTeamJoin mtj where mtj.joinedMember.id = :memberId")
    int deleteAllByJoinedMemberId(Long memberId);

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("delete from MemberTeamJoin mtj where mtj.joinedTeam.id = :teamId")
    int deleteAllByJoinedTeamId(Long teamId);

    @Query("""
            select
                case when(count(mtj) >= 1) then true else false end
            from MemberTeamJoin mtj
                where mtj.joinedMember.id = :memberId
                and mtj.joinedTeam.id = :teamId
            """)
    boolean existsByMemberTeamId(
            @Param("memberId") Long memberId,
            @Param("teamId") Long teamId
    );

    @Query("""
            select
                case when(count(mtj) >= 1) then true else false end
            from MemberTeamJoin mtj
                where mtj.joinedTeam.id = :teamId
            """)
    boolean existsByTeamId(
            @Param("teamId") Long teamId
    );

    @Query("""
            select
                case when(count(mtj) >= 1) then true else false end
            from MemberTeamJoin mtj
                where mtj.joinedTeam.id = :memberId
            """)
    boolean existsByMemberId(
            @Param("memberId") Long memberId
    );

    @Query("""
            select mtj from MemberTeamJoin mtj
                left join fetch mtj.joinedTeam
                left join fetch mtj.joinedTeam.teamInfo
                left join fetch mtj.joinedTeam.teamOwner
                left join fetch mtj.joinedTeam.teamOwner.info
                    where mtj.joinedMember.id = :memberId
            """)
    Page<MemberTeamJoin> findAllByMemberIdWithFetching(Long memberId, Pageable pageable);

    @Query("""
            select mtj from MemberTeamJoin mtj
                left join fetch mtj.joinedMember
                left join fetch mtj.joinedMember.info
                    where mtj.joinedTeam.id = :teamId
            """)
    Page<MemberTeamJoin> findAllByTeamIdWithFetching(Long teamId, Pageable pageable);

    @Query("""
            select mtj from MemberTeamJoin mtj
                where mtj.joinedMember.id = :memberId
                and mtj.joinedTeam.id = :teamId
            """)
    Optional<MemberTeamJoin> findByMemberIdAndTeamId(
            @Param("memberId") Long memberId,
            @Param("teamId") Long teamId
    );
}
