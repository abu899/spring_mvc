# Servlet

## 기본 생성

- `@WebServlet` annotation 사용
    - name: 서블릿 이름
    - urlPatterns: url 매핑
    - 중복을 허용하지 않는다
- HTTP 요청을 통해 `urlPatterns`에 매핑된 url이 호출되면 서블릿 컨테이너는 아래 메서드를 실행한다
    - `protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException`

> `application.properties`에 `logging.level.org.apache.coyote.http11=debug`를
> 추가하여 로그로 HTTP 요청 메시지를 확인할 수 있다.
> 개발 단계에서만 사용해야한다.

## HttpServletRequest

HTTP 요청 메시지를 개발자가 직접 파싱하기에는 불편함이 존재한다. 서블릿에서 HTTP 요청 메시지를 파싱하고 개발자에게 그 결과를 `HttpServletRequest` 객체에 담아서 제공해준다.

### HttpServletRequest의 부가기능

- 임시 저장소 기능
    - 해당 http 요청이 시작부터 끝날 때 까지 유지되는 임시 저장소 기능 제공
    - 저장
        - `request.setAttribute(name, value)`
    - 조회
        - `request.getAttribute(name)`
- 세션 관리 기능
    - `request.getSession(create: true)`

### Http 요청 데이터

클라이언트에서 서버로 데이터를 전달하는 방법은 주로 3가지 방법을 사용한다

1. GET
    - Query parameter 방식 (/url?username=ahn&age=30)
    - 메시지 바디 없이, url의 쿼리에 데이터를 포함해서 전달하는 방법
    - 검색 필터, 페이징등에서 많이 사용한다
2. POST
    - HTML form 방식 (contents-type:application/x-www-form-urlencoded)
    - 메시지 바디에 데이터(쿼리 파라미터 형식)를 전달하는 방법
    - 회원 가입, 상품 주문, HTML form에서 사용한다
3. Http Message Body
    - HTTP API 방식(JSON, XML, TEXT)
        - 데이터 형식은 JSON을 주로 사용한다
    - POST, PUT, PATCH

> 　content-type은 HTTP message body의 형식을 지정하는 것이다  
> GET url 쿼리 파라미터 형식은 메세지 바디를 사용하지 않기 때문에 content-type이 없다.
> 하지만 POST HTML form 형식은 메시지 바디에 데이터를 포함해서 보내기 때문에 전송 형식을 반드시 지정해야한다.

## HttpServletResponse

HTTP 응답메시지를 생성하는 역할을 담당한다.

- HTTP 응답코드 지정
- 헤더 생성
- 바디 생성

추가적으로 다음의 편의 기능 또한 제공한다

- Content-Type
- Cookie
- Redirect

### Http 응답 데이터

HTTP 응답 데이터도 요청 데이터와 마찬가지로 주로 세 가지 종류로 담아서 전달한다.

- 단순 텍스트 응답
    - response.getWriter().println("ok");
- HTML 응답
    - HTML 응답은 content-type을 `text/html` 지정
- HTTP-API
    - Message body json 응답
    - content-type을 `application/json`으로

## Servlet과 동적 HTML

　서블릿을 이용하면 자바코드를 이용해서 동적 HTML을 만들 수 있고 그걸 확인하는 작업을 진행해보았다. 정적 HTML 문서의 경우 매번 달라지는 입력에 따른 회원 정보의 저장이나 목록과 같은 동적 HTML을 만드는
것이 불가능하지만, 서블릿을 이용하면 이를 해결할 수 있다.  
　하지만, 자바 코드내에 HTML을 직접 입력해야하는 복잡하고 비효율적인 작업이 수반되게된다. 이를 극복하려면 어떻게 할까? HTML에서 `변경해야하는 부분만 자바코드로` 넣을 수 없을까?  
　이렇게 해서 나온것이 `Template Engine(템플릿 엔진)`으로 HTML 문서에서 필요한 곳만 코드를 적용해서 동적으로 변경이 가능하다 대표적인 템플릿 엔진에는 JSP, Thymeleaf,
Freemarker, Velocity등이 있다.

> JSP는 성능과 기능면에서 다른 템플릿 엔진과의 경쟁에 밀리고 점점 이용이 줄어드는 추세이다.

## 서블릿과 JSP의 한계

서블릿
- View 화면을 위한 HTML을 만드는 작업이 자바 코드내에 존재하여 가독성이 떨어지고 복잡해보인다
- JSP를 사용하여 view를 생성하는 HTML 작업을 자바 코드내에 제거하고, 동적으로 변경하는 부분을 JSP에서 자바코드를 적용하여 해결 했다

JSP
- 하지만 JSP코드를 살펴보면 HTML과 자바코드가 혼재되어 있는 것을 볼 수 있다
- 즉, 비지니스 로직과 화면을 보여주는 view영역이 하나의 JSP 파일에 작성되어 있는 것이다.
- 이는 JSP가 너무 많은 역할을 담당함을 알 수 있고, 프로젝트가 커짐에 따라 개별적인 유지보수가 어려워짐을 예측할 수 있다.

**MVC 패턴이 등장한 이유**