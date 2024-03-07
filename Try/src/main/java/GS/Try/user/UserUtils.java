package GS.Try.user;

import java.io.IOException;
import java.util.List;

public class UserUtils {

    private UserService userService;

    public UserUtils(UserService userService) {
        this.userService = userService;
    }

    //All Campus Data Search
    public void getAllCampus() throws IOException {
        String requsturi = "/v2/campus?page=";
        int idx = 1;
        while (true) {
            String campusData = userService.getJsonData(userService.getAccesToken(), requsturi + idx);
            if (campusData.isEmpty() || campusData.equals("[]")) {
                break;
            }
            System.out.println("page" + idx + " : " + campusData);
            idx++;
        }
    }

    //Campus All User Re Search include Pisciner
    public List<String> getCampusUserDataAll(int campus_id) throws IOException {
        String requsturi = "/v2/campus/" + campus_id + "/users?page=";
        int pageNum = 1;
        String myCampus = userService.getJsonData(userService.getAccesToken(), requsturi+pageNum);
        pageNum++;
        List<String> logins = userService.extractValuesToList(myCampus,"id");
        while (true){
            myCampus = userService.getJsonData(userService.getAccesToken(), requsturi+pageNum);
            if (myCampus == null || myCampus.isEmpty() || myCampus.equals("[]"))
                break;
            List<String> logs = userService.extractValuesToList(myCampus,"id");
            if (logs.isEmpty())
                break;
            System.out.println("pageNum = " + pageNum);
            pageNum++;
            logins.addAll(logs);
        }
        System.out.println("myCampus = " + myCampus);
        System.out.println("logins.size = " + logins.size());
        return logins;
    }

    private static String getCampusidPutCampusString(String campus) {
        String data;
        if (campus.equals("42Seoul") || campus.equals("42seoul")
                || campus.equals("Seoul") || campus.equals("seoul"))
            data = "29";
        else if (campus.equals("42Gyeongsan") || campus.equals("42gyeongsan")
                || campus.equals("Gyeongsan") || campus.equals("gyeongsan"))
            data ="69";
        else
            data = "0";
        return data;
    }

}
