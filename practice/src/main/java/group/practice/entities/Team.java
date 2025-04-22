package group.practice.entities;

import group.practice.entities.base.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "fetchOwner",
                attributeNodes = @NamedAttributeNode(value = "teamOwner")
        ),
        @NamedEntityGraph(
                name = "fetchTeamInfo",
                attributeNodes = @NamedAttributeNode(value = "teamInfo")
        )
})
public class Team extends TimeBase {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_M_ID")
    private Member teamOwner;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private TeamInfo teamInfo;

    public static Team getNew() {
        return new Team();
    }
}
