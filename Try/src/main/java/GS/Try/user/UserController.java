package GS.Try.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.*;

@RestController
public class UserController extends HttpServlet {

    @Value("${api-url}")
    private String url;

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public RedirectView Login(){
        return new RedirectView(url);
    }

    @GetMapping("/user")
    public RedirectView LoginSuccess(@RequestParam("code") String code , HttpServletRequest request,HttpServletResponse response) throws IOException{
        String me = userService.getAccessToken(code);
        userService.findMeAndSave(request, response, me);
        return new RedirectView("/Hello");
    }

    @GetMapping("Hello")
    public String Hello() throws IOException {
//        userService.getAllCampus();
//        return "Hello";
        return userService.getCampusUserDataAll(69).toString();
    }

    @GetMapping("DataAllReset")
    public String LoginSed(HttpServletRequest request, Model model) throws InterruptedException, IOException {
        userService.getAllUsersData();
//        return userService.getUserAllDataByAdmin(request, model);
        return "Hello";.
        gi
    }
}
