package group.practice.entities;

import group.practice.entities.base.*;
import group.practice.services.dto.request.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamInfo extends Information {

    private String description;
    private String representation;

    @Builder
    public TeamInfo(String name, String email, String description, String representation) {
        this.name = name;
        this.email = email;
        this.description = description;
        this.representation = representation;
    }

    public static TeamInfo getNew() {
        return new TeamInfo();
    }

    public TeamInfo setInfo(CreateTeamReq req) {
        if (req != null) {
            super.setBaseInfo(req.baseInfo());
            description = req.description();
            representation = req.representation();
        }

        return this;
    }
}
