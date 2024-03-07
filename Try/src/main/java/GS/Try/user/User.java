package GS.Try.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "member")
@Getter
@Setter
public class User {
    @Id
    private long id;
    private String login;
    private String grade;
    private String campus_id;
    private String large;
    private String small;
    private String micro;
    private int correction_point;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

