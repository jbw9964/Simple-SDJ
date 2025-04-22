package group.practice.entities.base;

import group.practice.services.dto.request.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "INFO_TYPE")
public abstract class Information extends TimeBase {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    protected Long id;

    protected String name;
    protected String email;

    protected void setBaseInfo(BaseInfoReq req) {
        if (req != null) {
            name = req.name();
            email = req.email();
        }
    }
}
