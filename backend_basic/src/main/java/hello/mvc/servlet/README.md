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