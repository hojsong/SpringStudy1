package GS.Try.user;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

//@RestController
@Controller
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
    public String Hello() throws IOException, InterruptedException {
        return "Hello";
    }

    @GetMapping("DataAllReset")
    public String LoginSed(HttpServletRequest request, Model model) throws InterruptedException, IOException {
        return userService.getUserAllDataByAdmin(request, model);
    }
}
