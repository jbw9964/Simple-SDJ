package group.practice.repositories;

import group.practice.entities.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface MemberRepository
        extends JpaRepository<Member, Long> {

    @EntityGraph(value = "fetchMemberInfo")
    @Query("select m from Member m where m.id = :id")
    Optional<Member> findByIdFetchInfo(Long id);

    @EntityGraph(value = "fetchMemberInfo")
    @Query("select m from Member m")
    Page<Member> findAllFetchInfo(Pageable pageable);
}
