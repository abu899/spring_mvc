package hello.mvc.frontcontroller.v4.controller;

import hello.mvc.frontcontroller.v4.ControllerV4;
import hello.mvc.servlet.domain.member.Member;
import hello.mvc.servlet.domain.member.MemberRepository;

import java.util.List;
import java.util.Map;

public class MemberListControllerV4 implements ControllerV4 {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        List<Member> members = memberRepository.findAll();

        model.put("members", members);

        return "members";
    }
}
