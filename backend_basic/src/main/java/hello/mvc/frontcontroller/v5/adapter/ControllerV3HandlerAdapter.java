package hello.mvc.frontcontroller.v5.adapter;

import hello.mvc.frontcontroller.ModelView;
import hello.mvc.frontcontroller.v3.ControllerV3;
import hello.mvc.frontcontroller.v5.MyHandlerAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV3HandlerAdapter implements MyHandlerAdapter {

    @Override
    public boolean support(Object handler) {
        return (handler instanceof ControllerV3);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV3 controller = (ControllerV3) handler;

        // request로 부터 parameter들을 map 형태로 저장
        Map<String, String> paramMap = createParamMap(request);
        // 컨트롤러들이 map에서부터 필요한 값들을 가져오게 한다(Not using request attribute)
        return controller.process(paramMap);
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName ->
                        paramMap.put(paramName, request.getParameter("paramName")));
        return paramMap;
    }
}
