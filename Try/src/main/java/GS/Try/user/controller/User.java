package GS.Try.user.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    //        login               intra id
    private String id;
    //        small               picture
    private String picutre;
    //        grade               Member / Learner
    private String grade;
    //        campus              캠퍼스
    private String campus;
    //        correction_point    Evaluation point
    private int evPoint;
}
