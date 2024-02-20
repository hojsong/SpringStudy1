package hello.servlet.web.servlet;

import hello.servlet.domain.Member;
import hello.servlet.domain.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "memberListServlet", urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {

    MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        List<Member> members = memberRepository.findAll();

        PrintWriter w = response.getWriter();
        w.write(
                "<html>" +
                        "<head>" +
                        "<meta charset=\"UTF-8\">" +
                        "<title>멤버 리스트</title>" +
                        "</head>" +
                        "<body>" +
                        "<a href=\"index.html\">메인</a>" +
                        "<table>" +
                        "   <thead>" +
                        "   <th>id</th>" +
                        "   <th>username</th>" +
                        "   <th>age</th>" +
                        "   </thead>" +
                        "   <tbody>");
        for (Member member : members) {
            w.write("<tr>" +
                    "<td>" + member.getId() + "</td>" +
                    "<td>" + member.getUsername() + "</td>" +
                    "<td>" + member.getAge() + "</td>" +
                    "</tr>");
        }
        w.write("</tbody>" +
                "</table>" +
                "</body>" +
                "</html>");
    }
}
