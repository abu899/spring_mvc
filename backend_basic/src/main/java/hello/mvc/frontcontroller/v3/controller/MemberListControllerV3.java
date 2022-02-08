package hello.mvc.frontcontroller.v3.controller;

import hello.mvc.frontcontroller.ModelView;
import hello.mvc.frontcontroller.MyView;
import hello.mvc.frontcontroller.v3.ControllerV3;
import hello.mvc.servlet.domain.member.Member;
import hello.mvc.servlet.domain.member.MemberRepository;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {

    MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        List<Member> members = memberRepository.findAll();

        ModelView modelView = new ModelView("members");
        modelView.getModel().put("members", members);

        return modelView;
    }
}
