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
public class MemberInfo extends Information {

    private String phone;
    private String comment;
    private Integer age;

    @Builder
    public MemberInfo(String name, String email, String phone, String comment, Integer age) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.comment = comment;
        this.age = age;
    }

    public static MemberInfo getNew() {
        return new MemberInfo();
    }

    public MemberInfo setInfo(CreateMemberReq req) {
        if (req != null) {
            super.setBaseInfo(req.baseInfo());
            phone = req.phone();
            comment = req.comment();
            age = req.age();
        }
        return this;
    }
}
