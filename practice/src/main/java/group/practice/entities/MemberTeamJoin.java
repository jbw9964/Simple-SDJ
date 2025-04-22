package group.practice.entities;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import java.time.*;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "fetchMember",
                attributeNodes = @NamedAttributeNode(value = "joinedMember"
                )),
        @NamedEntityGraph(
                name = "fetchTeam",
                attributeNodes = @NamedAttributeNode(value = "joinedTeam"
                ))
})
public class MemberTeamJoin {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_M_ID")
    private Member joinedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_T_ID")
    private Team joinedTeam;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinedAt;

    public static MemberTeamJoin of(Member joinedMember, Team joinedTeam) {
        MemberTeamJoin newJoin = new MemberTeamJoin();
        newJoin.joinedMember = joinedMember;
        newJoin.joinedTeam = joinedTeam;
        return newJoin;
    }
}
