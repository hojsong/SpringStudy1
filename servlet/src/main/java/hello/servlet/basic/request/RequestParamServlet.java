package hello.servlet.basic.request;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;

@WebServlet(name="requestParamServlet", urlPatterns = "/request-param")
public class RequestParamServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        GetParmasAllPrint(request, response);
    }

    private static void GetParmasAllPrint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("--- 전체 파라미터 조회 start ---");
        if(request.getParameterNames() != null){
            request.getParameterNames().asIterator()
                    .forEachRemaining(param -> System.out.println(param + ": " + request.getParameter(param))) ;
        }
        System.out.println("--- 전체 파라미터 조회 end ---");
        System.out.println();
        System.out.println("--- 단일 파라미터 조회 start ---");
        String username = request.getParameter("username");
        System.out.println("username = " + username);
        String age = request.getParameter("age");
        System.out.println("age = " + age);
        String game = request.getParameter("game");
        System.out.println("game = " + game);
        System.out.println("--- 단일 파라미터 조회 end ---");
        System.out.println();
        System.out.println("--- 이름이 같은 복수 파라미터 조회 start ---");
        String[] usernames = request.getParameterValues("username");
        for(String name : usernames){
            System.out.println("username = " + name);
        }
        System.out.println("--- 이름이 같은 복수 파라미터 조회 end ---");
        System.out.println();
        response.getWriter().write("ok");
    }
}
