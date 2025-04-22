package group.practice.repositories;

import group.practice.entities.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface TeamRepository
        extends JpaRepository<Team, Long> {

    @Modifying(
            flushAutomatically = true,
            clearAutomatically = true
    )
    @Query("update Team set teamOwner = null where teamOwner.id = :ownerId")
    int updateOwnerAsNullOnMemberId(Long ownerId);

    @EntityGraph(value = "fetchOwner")
    @Query("select t from Team t where t.id = :id")
    Optional<Team> findByIdFetchOwner(Long id);

    @Query("""
            select t from Team t
                left join fetch t.teamInfo
                left join fetch t.teamOwner
                left join fetch t.teamOwner.info
                where t.id = :id
            """)
    Optional<Team> findByIdFetchInfoAndOwner(Long id);

    @Query("""
            select t from Team t
                left join fetch t.teamInfo
                left join fetch t.teamOwner
                left join fetch t.teamOwner.info
            """)
    Page<Team> findAllFetchInfoAndOwner(Pageable pageable);
}
