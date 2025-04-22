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
@NamedEntityGraph(
        name = "fetchMemberInfo",
        attributeNodes = @NamedAttributeNode(value = "info")
)
public class Member extends TimeBase {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Long id;

    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JoinColumn(name = "FK_MIF_ID")
    private MemberInfo info;

    public static Member getNew() {
        return new Member();
    }
}
