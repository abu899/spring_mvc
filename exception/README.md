# Exception

## In 서블릿

### 서블릿 예외처리

서블릿은 두가지 방식으로 예외 처리를 지원한다
1. `Exception`
   - 자바 직접 실행
     - 자바의 메인 메서드를 직접 실행하는 경우, `main` 쓰레드가 실행
     - 예외가 발생한 경우 콜 스택을 따라 자신을 호출한 상위 메서드로 계속 올라가게 되고 결국 `main()`을 만나 예외 정보를 넘기고 종료된다
   - 웹 어플리케이션
     - 사용자 요청별로 쓰레드가 할당, 서블릿 컨테이너 안에서 실행
     - 예외가 발생한 경우 try-catch로 예외를 처리하면 문제가 없지만, 만약 이를 처리하지 않은 경우 어떻게 될까?
     - 즉 예외가 상위 메서드로 계속 전달되며 서블릿 밖가지 전달되면?
       - `컨트롤러(Excpetion) -> 인터셉터 -> 서블릿 -> 필터 -> WAS(여기까지 전파)`
     - WAS까지 예외가 전달되는데, 여기까지 올라오게 되면 어떻게 처리해야될까?
       - 이렇게 WAS까지 예외가 전달되면, 서버 내부에서 처리할 수 없는 오류로 생각해 `Internal Server Error(500)`를 반환한다
       - 반면 페이지가 없는 리소스를 호출하면 `Not Found(404)`에러를 반환한다
2. `response.sendError`: 파라미터 (HTTP 상태코드, 오류메시지)
   - `HttpServletResponse`가 제공하는 `sendError` 메서드 또한 사용가능하다
   - 이 메서드를 사용하면 HTTP 상태코드와 오류메시지를 추가할 수 있다.
     - `컨트롤러(response.sendError) -> 인터셉터 -> 서블릿 -> 필터 -> WAS(sendError 호출 기록 확인)`
     - 서블릿 컨테이너는 사용자에게 보여주기 전에 `sendError()`가 호출되었는지 확인하고, 오류코드에 맞추어 오류페이지를 보여준다.

즉, `Exception`이 발생하면 무조건 500  에러, 내가 직접 `sendError`를 통해 처리를 했으면 오류코드에 맞춰 페이지가 등장한다.

### 오류 페이지의 작동 원리

```text
예외 발생 흐름
컨트롤러(예외 발생, sendError) -> 인터셉터 -> 서블릿 -> 필터 -> WAS
```

WAS는 예외가 발생하면, 해당 예외를 처리하는 오류 페이지(ErrorPage) 정보를 확인한다
- `new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404")`

```text
오류페이지 요청 흐름
WAS /error-page/404 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/404) -> View
```
- 이처럼 해당 예외 페이지가 존재한다면, WAS에서 오류페이지를 출력하기 위해 다시 요청이 진행된다.
- 중요한건 HTTP 요청이 생긴게 아닌, 서버 내부에서 오류페이지를 찾기 위해 추가적인 호출을 진행하는 것
  - 따라서, 웹브라우저(클라이언트)는 이것을 인지하지 못한다
- WAS는 단순히 오류 페이지를 호출하는 것 뿐만 아니라, 오류 정보 또한 넘겨준다
  - `request`의 `attribute`를 통해

### 서블릿 예외처리 - 필터

```text
예외 발생 흐름과 페이지 요청 흐름
컨트롤러(예외 발생, sendError) -> 인터셉터 -> 서블릿 -> 필터 -> WAS
WAS /error-page/404 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 
   -> 컨트롤러(/error-page/404) -> View
```
위와 같이 오류가 발생하면 오류페이지 호출을 위해 WAS 내부에서 다시한번 호출이 발생한다.
이때, 필터, 서블릿, 인터셉터도 다시 호출되는 상황이 발생하는데, 로그인 인증과 같은 처리는 이미 완료했기에 추가적인 호출은 비효율적이게 된다.  
따라서, 클라이언트로부터 발생한 `정상 요청`인지, 오류페이지 출력을 위한 `내부 요청`인지 구별할 수 있어야한다.

- DispatcherType
  - 이런 경우를 위해 `dispatcherType()`라는 옵션이 제공된다.
  - 고객이 요청하게 되면 `dispatcherType`이 `REQUEST`
  - 내부 에러에 따라 호출되게 되면 `dispatcherType`은 `ERROR`
  - 외의 Type
    - `FORWARD`: 서블릿에서 다른 서블릿이나 JSP를 호출할 때(`RequestDispatcher.forward()`)
    - `INCLUDE`: 서비릇에서 다른 서블릿이나 JSP의 결과를 포함할 때(`RequestDispatcher.include()`)
    - `ASYNC`: 서블릿 비동기 호출
  - 즉, 클라이언트의 요청이 있는 경우에만 필터를 적용하고 싶다면, `FilterRegistrationBean.setDispatcherType`에 `REQUEST`만을 지정해준다.
    - 디폴트가 `REQUEST`

### 서블릿 예외처리 - 인터셉터

```java
class WebConfig implements WebMvcConfigurer {
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LogInterceptor())
            .order(1)
            .addPathPatterns("/**")
            .excludePathPatterns("/css/**", "/error", "*.ico", "/error-page/**");
  }
}
```
스프링 인터셉터의 경우 따로 `dispatcherType`을 지정하는 것이 아닌,
인터셉터를 등록할 때 `excludePatterns`에서 `error-page의 경로`를 넣어 중복 호출을 피할 수 있다.

### 필터, 인터셉터 예외처리 정리

- 필터는 DispatchType 으로 중복 호출 제거 ( dispatchType=REQUEST )
- 인터셉터는 경로 정보로 중복 호출 제거( excludePathPatterns("/error-page/**") )

```text
1. WAS(/error-ex, dispatchType=REQUEST) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러
2. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
3. WAS 오류 페이지 확인
4. WAS(/error-page/404, dispatchType=ERROR) -> 필터(x) -> 서블릿 -> 인터셉터(x) ->
   컨트롤러(/error-page/404) -> View
```

### 스프링 부트의 오류페이지

지금까지 예외 처리 페이지를 만들기 위해서 다음과 같은 복잡한 과정을 거쳤다.
- `WebServerCustomizer`를 만들고
- 예외 종류에 따라서 `ErrorPage`를 추가하고
- 예외 처리용 컨트롤러 `ErrorPageController`를 만듬

스프링 부트에서는 개발자는 오류 페이지만 리소스에 추가해주면 된다
- 이때 `/error`라는 경로로 기본 오류 페이지를 설정
- 이외의 `ErrorPage` 및 `BasicErrorController`등은 스프링 부트가 자동으로 등록한다

스프링 `BasicErrorController`가 제공하는 정보
```text
timestamp: Fri Feb 05 00:00:00 KST 2021
status: 400
error: Bad Request
exception: org.springframework.validation.BindException
trace: 예외 trace
message: Validation failed for object='data'. Error count: 1
errors: Errors(BindingResult)
path: 클라이언트 요청 경로 (`/hello`)
```
하지만 모든 정보를 사용자에게 보여줄 필요는 없고 필요한 정보만 간단하게 전달하는게 낫다

---

## API 예외처리

HTML 페이지의 경우 4xx, 5xx 같은 오류페이지를 제공하면 되지만, API의 경우 오류 상황에 맞는 오류 응답
스펙을 정하고, JSON을 데이터를 전달해야한다.

- 현재 API를 호출해서 정상작동을 하면 JSON 응답이 오지만, 에러가 발생하면 우리가 미리 만들어둔 오류페이지 HTML이 반환된다.
- 하지만, API 예외처리에서는 JSON 에러가 반환되길 기대하며, 웹브라우저가 아니면 HTML을 받아도 할 수 있는 것이 없다.

### 스프링 부트 기본 오류 처리

`BasicErrorController`를 사용한 스프링 부트 기본 오류 처리.

- 기본적으로 `BasicErrorController`를 사용하면, `Accept`에 따라 받을 수 있는 에러가 달라진다
  - `HTML`인 경우는 `ModelAndView`로, 그 이외의 경우 `ResponseEntity`로 디폴트 에러를 받게된다.
  - `BasicErrorController` 내 `errorHtml()`과 `error()`가 호출된다
- `BasicErrorController`는 HTML 페이지를 제공하는 경우에는 편리하지만, API는 상황이 조금 다르다
  - API, 각각의 컨트롤러나 예외마다 다른 응답 결과를 출력해야하는 경우가 있기 때문이다.
  - 결과적으로 API 예외는 세밀하고 복잡하기에 `BasicErrorController`는 HTML 화면을 처리할 때만 사용하고,
  API 오류는 `@ExceptionHandler`를 사용하는게 더 나은 방법이다.

### HandlerExceptionResolver

<p align="center"><img src="/img/1.png" width="80%"></p>

<p align="center"><img src="/img/2.png" width="80%"></p>

스프링 MVC는 컨트롤러 밖으로 예외가 던져진 경우, 예외를 해결하고 새로 정의할 수 있는 방법을 제공한다.

- 활용
  - 예외를 `response.sendError(xxx)` 호출로 변경해서 상태 코드에 따른 오류 처리를 하도록 위임
    - `sendError` 대신, response의 데이터를 넣어서 처리하면 서블릿 컨테이너까지 에러가 전달되지 않게 할 수 있다.
    - 즉, `ExceptionResolver`를 사용하면 `예외가 발생해도 정상 흐름으로 변경`시킬 수 있는 것이 핵심
    - `UserHandlerExceptionResolver` 참조
  - 뷰 템플릿 처리
    - ModelAndView를 채워서 예외에 따른 오류 화면을 렌더링해서 제공
  - API 응답 처리
    - `response.getWriter().println(xxx)`처럼 HTTP 응답 바디에 직접 데이터를 넣어주는 것도 가능하다.
    - JSON으로 응답하면 API 응답처리 또한 가능하다

> WebConfig를 등록할 때, `configureHandlerExceptionResolver`를 사용하면 스프링이 기본으로 등록하는
> `ExceptionResolver`가 제거되므로 `extendHandlerExcpetionResolver`를 사용하자.

### 스프링이 제공하는 ExceptionResolver

스프링 부트는 기본적으로 `ExceptionResolver`를 제공하며 `HandlerExceptionReloverComposite`에 다음 순서로 등록한다

1. `ExceptionHandlerExceptionResolver`
   - `@ExceptionHandler`를 처리한다
   - API 예외처리는 대부분 이 기능으로 해결한다
2. `ResponseStatusExceptionResolver`
   - HTTP 상태코드를 지정해준다
   - ex) `@ResponseStatus(value = HttpStatus.NO_FOUND)`, `BadRequestException` 참조
   - `messages.properties`를 이용해 `reason`을 코드화 할 수 있다
   - 또한 `ResponseStatusException`에도 동작한다
3. `DefaultHandlerExceptionResolver` -> 우선순위가 가장 낮다
   - 스프링 내부에서 발생하는 기본 예외를 처리한다
   - 대표적으로 타입이 맞지 않으면 내부에서 `TypeMismatchException`이 발생한다

### ExceptionHandlerExceptionResolver

웹 브라우저에서 HTML 화면을 제공할 떄는 `BasicErrorController`를 사용하는게 편하다.
하지만, API는 같은 예외라도 컨트롤러에 따라 예외 응답을 다르게 내려주어야 할 수 있다.

- API 예외 처리에서 어려운 점
  - `HandlerExceptionResolver`는 `ModelAndView`를 반환해야 했다.
  - API 응답을 위해선 `HttpServletResponse`에 직접 응답 데이터를 넣어줘야한다
    - `UserHandlerExceptionResolver` 참조
  - 특정 컨트롤러에서 발생하는 예외를 별도로 처리하기 어렵다
- `@ExceptionHandler`
  - `@ExceptionHandler` 어노테이션을 선언하고, `해당 컨트롤러`에서 처리하고 싶은 예외를 지정해준다
  - 지정한 예외 또는 그 예외의 자식 클래스는 모두 잡을 수 있다
  - 다양한 파라미터와 응답을 제공한다 -> [Link](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-exceptionhandler-args)
    
`@ExceptionHandler`의 실행의 흐름

1. 컨트롤러를 호출한 결과 어떤 예외 발생해 컨트롤러 밖으로 던져진다.
2. 예외가 발생했으로 `ExceptionResolver`가 작동하고, 가장 우선순위가 높은 `ExceptionHandlerExceptionResolver`가 실행된다.
3. `ExceptionHandlerExceptionResolver`는 해당 컨트롤러에 발생한 예외를 처리할 `@ExceptionHandler`가 있는지 확인한다.
4. `@RestController` 이므로  HTTP 컨버터가 사용되고, 응답이 JSON으로 반환된다.

> 만약 다른 컨트롤러에도 똑같이 적용하고 싶으면 복사해서 붙여넣기를 해야할까?

### @ControllerAdvice

`@ExceptionHandler`를 사용하면 예외를 간단히 처리가능하지만, 정상코드와 예외 처리 코드가 하나의 컨트롤러에 섞이게된다.
이때 `@ControllerAdvice`와 `@RestControllerAdvice`를 사용하면 이를 분리할 수 있다.
추가적으로 컨트롤러 간 공통으로 사용하고 싶은 부분 또한 복사없이 적용할 수 있다.

- 기본적으로 `@ControllerAdvice`는 지정한 여러 컨트롤러에 `@ExceptionHandler`, `@InitBinder` 기능을 부여해준다
  - 대상을 지정하지 않으면 모든 컨트롤러에 적용된다

대상 컨트롤러 지정 방법 -> [공식문서 Link](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-anncontroller-advice)
```text
// Target all Controllers annotated with @RestController
@ControllerAdvice(annotations = RestController.class)
public class ExampleAdvice1 {}

// Target all Controllers within specific packages
@ControllerAdvice("org.example.controllers")
public class ExampleAdvice2 {}

// Target all Controllers assignable to specific classes
@ControllerAdvice(assignableTypes = {ControllerInterface.class,AbstractController.class})
public class ExampleAdvice3 {}
```
