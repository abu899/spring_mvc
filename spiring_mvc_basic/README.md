# Spring MVC

## Spring MVC의 구조

<p align="center"><img src="/img/mvc_structure.png" width="80%"></p>

### Backend basic에서 만든 MVC와 Spring MVC의 맵핑

| Backend basic MVC | Spring MVC | 
|-------------------|---|
|FrontController | DispatcherServlet|
|handlerMappingMap | HandlerMapping|
|MyHandlerAdapter | HandlerAdapter|
|ModelView | ModelAndView|
|viewResolver | ViewResolver|
|MyView | View|

## DispatcherServlet

스프링 MVC 역시 Front controller 패턴으로 구현되어 있다. 우리가 만든 front-controller의 역할을 담당하는 것이
`DispatcherServlet`이며, 우리가 만든 `FrontController`의 내용은 `doDispatch`라는 method를 확인해보면 보다 확실한 흐름을 이해할 수 있다.

```java
public class DispatcherServlet extends FrameworkServlet {

    protected void doDispatch(HttpServletRequest request, HttpServletResponse
            response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;
        ModelAndView mv = null;

        // 1. 핸들러 조회
        mappedHandler = getHandler(processedRequest);
        if (mappedHandler == null) {
            noHandlerFound(processedRequest, response);
            return;
        }

        // 2. 핸들러 어댑터 조회 - 핸들러를 처리할 수 있는 어댑터
        HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

        // 3. 핸들러 어댑터 실행 -> 4. 핸들러 어댑터를 통해 핸들러 실행 -> 5. ModelAndView 반환
        mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
        processDispatchResult(processedRequest, response, mappedHandler, mv,
                dispatchException);
    }

    private void processDispatchResult(HttpServletRequest request,
                                       HttpServletResponse response, HandlerExecutionChain mappedHandler, ModelAndView
                                               mv, Exception exception) throws Exception {
        // 뷰 렌더링 호출
        render(mv, request, response);
    }

    protected void render(ModelAndView mv, HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        View view;
        String viewName = mv.getViewName();

        // 6. 뷰 리졸버를 통해서 뷰 찾기, 7. View 반환
        view = resolveViewName(viewName, mv.getModelInternal(), locale, request);

        // 8. 뷰 렌더링
        view.render(mv.getModelInternal(), request, response);
    }
}
```

### 동작 흐름

1. 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러)를 조회한다.
2. 핸들러를 실행할 수 있는 핸들러 어댑터를 조회한다.
3. 핸들러 어댑터를 실행한다.
4. 핸들러 어댑터가 실제 핸들러를 실행한다.
5. 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해서 반환한다.
6. viewResolver를 찾고 실행한다. JSP의 경우 `InternalResourceViewResolver`가 자동 등록된다.
7. viewResolver는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체를 반환한다. JSP의 경우 `InternalResourceView(JstlView)` 를 반환하는데, 내부에
   forward() 로직이 있다.
8. 뷰를 통해서 뷰를 렌더링 한다

## HandlerMapping and HandlerAdapter

어떤 컨트롤러가 호출되려면 2가지가 필요하다

1. HandlerMapping
   - 핸들러 매핑에서 원하는 컨트롤러를 찾을 수 있어야한다
   - ex) 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑이 필요
2. HandlerAdpater
   - 핸들러 매핑으로 찾은 핸들러를 실행할 수 있는 어댑터가 필요하다

### HandlerMapping

핸들러 매핑의 우선순위

1. RequestMappingHandlerMapping
- `@RequestMapping`을 사용하는 매핑 방법
2. BeanNameUrlHandlerMapping
- Spring Bean의 이름으로 핸들러를 찾는 방법

### HandlerAdapter

핸들러 어댑터의 우선순위 

1. RequestMappingHandlerAdapter
- `@RequestMapping`에서 사용
2. HttpRequestHandlerAdapter
- `HttpRequestHandler`에서 처리
3. SimpleControllerHandlerAdapter
- 과거 Controller interface 처리

## ViewResolver

스프링 부트가 자동으로 등록하는 `ViewResolver` 우선 순위
1. BeanNameViewResolver
   - Bean 이름으로 뷰를 찾아서 반환하는 방법
2. InternalResourceViewResolver
   - JSP를 처리할 수 있는 뷰를 반환

## 사용

### @Controller

- 스프링이 자동으로 스프링 빈으로 등록한다
- 스프링 MVC에서 어노테이션 기반 컨트롤러로 인식한다
  - `RequestMappingHandlerMapping`에서 매핑 정보로 인식한다

### @RequestMapping

- 요청 정보로 매핑
- 해당 URL이 호출되면 매핑된 메서드를 호출한다

> `RequestMappingHandlerMapping`은 `@RequestMapping` 또는 `@Controller`가 `클래스 레벨`에
> 붙어 있는 경우 매핑 정보로 인식한다