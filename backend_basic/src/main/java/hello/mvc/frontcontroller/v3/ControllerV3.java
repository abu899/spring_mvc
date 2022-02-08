package hello.mvc.frontcontroller.v3;

import hello.mvc.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerV3 {
    ModelView process(Map<String, String> paramMap);
}
