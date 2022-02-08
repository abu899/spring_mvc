package hello.mvc.frontcontroller.v1.controller;

import hello.mvc.frontcontroller.v1.ControllerV1;
import hello.mvc.servlet.domain.member.Member;
import hello.mvc.servlet.domain.member.MemberRepository;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class MemberListControllerV1 implements ControllerV1 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Member> members = memberRepository.findAll();

        request.setAttribute("members", members);

        String writePath = "/WEB-INF/views/";
        RequestDispatcher dispatcher = request.getRequestDispatcher(writePath);
        dispatcher.forward(request, response);
    }
}
