package GS.Try.user;

import GS.Try.oauth42.Oauth2Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
public class UserService {

    private final Oauth2Service oauth2Service;
    private final UserRepository userRepository;

    @Value("${secret-key}")
    private String sek;
    @Value("${api-url}")
    private String url;
    @Value("${client-id}")
    private String clientId;
    @Value("${client-secret}")
    private String clientSecret;
    @Value("${redirect-uri}")
    private String reurl;

    private String AccesToken;
    private int activateEvPoint;
    private List<String> userIdList;
    private Key key;
    @Autowired
    public UserService(UserRepository userRepository, Oauth2Service oauth2Service){
        this.userRepository = userRepository;
        this.oauth2Service = oauth2Service;
    }

    public String getUserAllDataByAdmin(HttpServletRequest request, Model model) throws IOException, InterruptedException {
        String Token = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    Token = cookie.getValue();
                }
            }
        }

        System.out.println("Token = " + Token);
        if (Token.isEmpty()) {
            model.addAttribute("message", "Failed to retrieve token information.");
            return "Error";
        }


        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(Token);
        System.out.println(jws.getBody());

        getAllUsersData();
        model.addAttribute("message", "Data All Search Complete");
        return "Complete";
    }

    public void getAllCampus() throws IOException {
        String requsturi = "/v2/campus?page=";
        int idx = 1;
        while (true) {
            String campusData = getJsonData(this.AccesToken, requsturi + idx);
            if (campusData.isEmpty() || campusData.equals("[]")) {
                break;
            }
            System.out.println("page" + idx + " : " + campusData);
            idx++;
        }
    }


    // 피시너 데이터까지 다긁어온다.
    public List<String> getCampusUserDataAll(int campus_id) throws IOException {
        String requsturi = "/v2/campus/" + campus_id + "/users?page=";
        int pageNum = 1;
        String myCampus = getJsonData(this.AccesToken, requsturi+pageNum);
        pageNum++;
        List<String> logins = extractValuesToList(myCampus,"id");
        while (true){
            myCampus = getJsonData(this.AccesToken, requsturi+pageNum);
            if (myCampus == null || myCampus.isEmpty() || myCampus.equals("[]"))
                break;
            List<String> logs = extractValuesToList(myCampus,"id");
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

    public void findMeAndSave(HttpServletRequest request, HttpServletResponse response, String me) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = mapper.readValue(me, User.class);
        JsonNode rootNode = mapper.readTree(me);

        if (rootNode.toString().contains("\"grade\":\"Learner\""))
            user.setGrade("Learner");
        else if (rootNode.toString().contains("\"grade\":\"Member\""))
            user.setGrade("Member");
        else
            user.setGrade("???");

        Optional<User> originUser = userRepository.findById(user.getId());
        if (originUser.isEmpty() || originUser == null) {
            System.out.println("No Search USer");
            userRepository.save(user);
        }
        else {
            if (originUser.get().getId().equals(user.getId())) {
                if (!originUser.get().getLogin().equals(user.getLogin()))
                    originUser.get().setLogin(user.getLogin());
                if (!originUser.get().getGrade().equals(user.getGrade()))
                    originUser.get().setGrade(user.getGrade());
            } else {
                userRepository.save(user);
            }
        }

        byte[] byteKey = this.sek.getBytes(StandardCharsets.UTF_8);
        // SecretKeySpec으로 키 생성
        this.key = new SecretKeySpec(byteKey, "HmacSHA256");
        System.out.println("key = " + key);

        String jws = Jwts.builder()
                .setSubject("Joe")  // 주제 설정
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))  // 만료 시간 설정
                .claim("id", user.getId())
                .claim("name", user.getLogin())
                .claim("grade", user.getGrade())
                .signWith(key)  // 비밀 키로 서명
                .compact();  // JWT 문자열 생성

        System.out.println("JWT: " + jws);

        Cookie[] cookies = request.getCookies();

        if (cookies != null){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    cookie.setMaxAge(0);
                }
            }
        }

        Cookie cookie = new Cookie("token", jws);
        // 쿠키 설정
        cookie.setMaxAge(60 * 10); // 쿠키 유효 시간을 1시간으로 설정
        cookie.setHttpOnly(true);  // JavaScript를 통한 쿠키 접근 차단
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    public String getAccessToken(String code) {
        String accessToken = oauth2Service.getAccessTokenWithCode(code, clientId, clientSecret, reurl);
        System.out.println("accessToken = " + accessToken);
        // 액세스 토큰을 이용하여 'v2/me'에 GET 요청을 보냅니다.
        String me = oauth2Service.getMe(accessToken);
        System.out.println("me = " + me);
        this.AccesToken = accessToken;
        return me;
    }

    public void getAllUsersData() throws IOException, InterruptedException {
        String requsturi = "/v2/users/";
        activateEvPoint = 0;

        userIdList = getCoalitionUsersDataPutCampus("42Seoul");
        putCampusOfUsersData(requsturi);

        userIdList = getCoalitionUsersDataPutCampus("42Gyeongsan");
        putCampusOfUsersData(requsturi);

        System.out.println("activateEvPoint = " + activateEvPoint);
    }

    private void putCampusOfUsersData(String requsturi) throws JsonProcessingException, InterruptedException {
        for (String idx : userIdList) {
            String userData = getJsonData(this.AccesToken, requsturi + idx + "/coalitions");
            Thread.sleep(1000);
//            System.out.println("userData = " + userData);
            if(userData != null && !userData.isEmpty() && !userData.equals("[]")) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                User user = mapper.readValue(userData, User.class);
                JsonNode rootNode = mapper.readTree(userData);
                if (rootNode.toString().contains("\"grade\":\"Learner\""))
                    user.setGrade("Learner");
                else if (rootNode.toString().contains("\"grade\":\"Member\""))
                    user.setGrade("Member");
                else
                    user.setGrade("???");

                if (user.getGrade().equals("Learner") || user.getGrade().equals("Member")) {
                    String login = user.getLogin();
                    String grade = user.getGrade();
                    int correction_point = user.getCorrection_point();

                    System.out.println(idx + " : " + login + " / " + grade + " / " + correction_point);
                    System.out.println("userData = " + userData);
                    if (user.getGrade().equals("Learner"))
                        activateEvPoint += user.getCorrection_point();
                    else if (user.getGrade().equals("MEMBER") && user.getCorrection_point() < 0)
                        activateEvPoint -= user.getCorrection_point();
                    userRepository.save(user);
                }
            }
        }
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

    public List<String> getCoalitionUsersDataPutCampus(String campus) throws IOException, InterruptedException {
        // 건 곤 감 리 //
        String data = null;
        data = getCoalitionsPutCampusString(campus);
        if (data == null) return (null);
        userIdList = Arrays.asList(data.split(", "));
        String requsturi = "/v2/coalitions/";
        List<String> userList = new ArrayList<>();
        for (String idx : userIdList) {
            int pagenum = 1;
            while (true) {
                String userData = getJsonData(this.AccesToken, requsturi + idx + "/coalitions_users?page=" + pagenum);
                List<String> logins = extractValuesToList(userData, "user_id");
                Thread.sleep(1000);
                pagenum++;
                if (userData.isEmpty() || userData.equals("[]") || logins.isEmpty()) {
                    break;
                }
                userList.addAll(logins);
            }
        }
        return userList;
    }

    private static String getCoalitionsPutCampusString(String campus) {
        String data;
        if (campus.equals("42Seoul") || campus.equals("42seoul")
                || campus.equals("Seoul") || campus.equals("seoul"))
            data ="85 , 86 , 87 , 88";
        else if (campus.equals("42Gyeongsan") || campus.equals("42gyeongsan")
                || campus.equals("Gyeongsan") || campus.equals("gyeongsan"))
            data ="454 , 455 , 456 , 457";
        else
            return null;
        return data;
    }

    // Token, RequestURI(endpoint) (Json)userData Get
    public String getJsonData(String accessToken, String requsturi) {
        String myCampus = oauth2Service.getUri(accessToken, requsturi);
        return myCampus;
    }

    // Json Data + key -> key 에 대한 Value들이 List형태로 나온다.
    public List<String> extractValuesToList(String json, String key) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        List<String> values = new ArrayList<>();
        root.forEach(node -> {
            if (node.has(key)) {
                values.add(node.get(key).asText());
            }
        });

        return values;
    }


//    public String extractValueFromJson(String json, String key) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode root = mapper.readTree(json);
//
//        if (root.has(key)) {
//            return root.get(key).asText();
//        } else {
//            return null;
//        }
//    }

}
