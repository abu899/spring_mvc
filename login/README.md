# Login

## Login with Cookie

로그인 시 쿠키를 이용해 로그인을 유지

- 영속 쿠키
    - 만료 날짜를 입력하면 해당 날짜까지 유지
- 세션 쿠키
    - 만료 날짜를 생략하면 브라우저 종료시 까지만 유지

### 쿠키 생성

로그인 성공 시, 쿠키를 생성하고 웹브라우저가 종료전까지 회원의 `id`를 서버에 계속 보내준다

```text
Cookie cookie = new Cookie("memberId", String.valueOf(member.getId()));
response.addCookie(cookie);
```

쿠기 값을 받기 위해선 `@CookieValue` 어노테이션을 이용하고, 쿠키가 없는 데이터도 받기 위해 `required=fasle`로 지정해준다.

```java
class test {
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId) {
    }
}
```

### 쿠키 삭제

쿠키 삭제를 위해 `setMaxAge`를 0으로 설정한 후, `HttpServletResponse`에 쿠키를 설정해준다.
```java
class test {
    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
```

### 쿠키의 보안 문제

1. 쿠키는 임의로 변경될 수 있다
   - 쿠키의 값은 결국 클라이언트에서 서버로 전달하는 것이기 때문에 변조가 가능하다
   - 만약 id값이 쿠키로 넘어오게 되서 변조하게 되면, 다른 사용자로 변경되는 문제가 발생한다
2. 쿠키에 보관된 정보는 훔칠 수 있다
   - 앞서 말했듯, 쿠키는 클라이언트에 저장되기 때문에 개인정보가 존재한다면?
   - 웹브라우저(클라이언트)에 저장된 데이터가 네트워크 전송 구간에서 도난당할 수 있다.
   - 도난당한 쿠키 정보는 지속적으로 사용될 수 있다

### 보안 문제의 대안

- 쿠키에 중요한 값을 노출하지 않아야하며, 예측 불가능한 토큰 값을 노출하고 이를 매핑해서 사용해야한다
  - 예측 가능한 1,2,3,4... 등의 값이 아닌 랜덤한 값을 사용
- 서버에서 토큰을 관리해야한다.
- 토큰이 도난당해도, 시간이 지나면 사용할 수 없도록 서버에서 해당 토큰의 만료시간을 짧게 유지해야한다.
  - 도난이 의심되는 경우 서버에서 해당 토큰을 강제로 제거.

## Login with Session

사실 세션이라는게 특별한 기능이라기 보다는 `쿠키이지만 클라이언트가 아닌 서버에서 데이터를 유지하는 방법`

### 장점

- 변조 가능한 쿠키 값 대신, 예상 불가능한 세션 id를 사용
- 쿠키를 보관하는 클라이언트가 도난 당해도, 실제 정보가 서버에 존재하기에 무관
- 일정 시간이 지나면 세션이 만료되기에, 일정 시간 이후에는 사용 불가.

### 세션 직접 만들기

- 세션 생성
  - sessionId 생성
  - 세션 저장소에 sessionId와 보관할 값 저장
  - sessionId로 응답 쿠키를 생성해서 클라이언트에 전달
- 세션 조회
- 세션 만료
  - 클라이언트가 요청한 sessionId 쿠키 값으로 세션 저장소에서 제거

### Servlet Http Session

서블릿이 제공하는 `HttpSession`도 결국 우리가 만든 `SessionManager`와 같은 방식으로 동작한다

- `HttpServletRequest.getSession()`
  - 세션이 있으면 기존 세션 반환
  - 세션이 없다면 새로운 세션을 생성해서 반환
  - `getSession`의 파라미터가 `false`라면 세션이 없을 때는 새로운 세션을 생성하지 않는다.
  - `false`는 기존 세션으로 객체를 찾거나 `invalidate`할때 사용
- 스프링은 세션을 편리하게 사용하기 위한 `@SessionAttribute` 어노테이션이 존재
  - 세션을 찾아올 때 사용하면 되며, 세션을 새로 생성하지 않는다

### TrackingModes

`HttpSession`을 통해 로그인을 진행하게 되면 url뒤에 세션 아이디가 나타나는 걸 확인할 수 있다.

- 웹브라우저가 쿠키를 지원하지 않을 때 쿠키 대신 URL을 통해 세션을 유지하는 방법
  - 서버 입장에서는 쿠키를 지원하는지 알 수 없기에 쿠키 값도 전달하고 `jsessionId`도 함께 전달
- 하지만, 이 방법을 사용하기 위해선 세션을 유지하는 모든 url 뒤에 동일한 세션 아이디를 붙여줘야한다.
- 타임리프 같은 템플릿 엔진을 통해 자동으로 포함시킬 수 있다
- URL 전달 방식을 끄고 쿠키를 통해서만 세션을 유지하고자 한다면 옵션을 넣어주면 된다.
  - `server.servlet.session.tracking-modes=cookie`

### 세션 정보와 타임아웃

- 세션 정보
  - `sessionId`
  - `maxInactiveInterval` : 세션 유효시간(초)
  - `creationTime` : 세션 생성일시
  - `lastAccessedTime` 
    - 세션과 연결된 사용자가 최근에 접근한 시간
    - 클라이언트에서 서버로 `sessionId(JSESSIONID)`를 요청한 경우에 갱신
  - `isNew` : 새로 생성된 세션인가
- 세션 타임아웃
  - 기본적으로 세션은 사용자가 직접 `session.invalidate()`가 호출되는 경우 삭제
  - 하지만 HTTP는 **비연결성**이므로 서버 입장에서는 클라이언트가 종료된건지 아닌지 알 수 없음
    - 즉 서버입장에서 세션을 삭제해야되는지 아닌지 알 수 없음.
    - 세션은 기본적으로 메모리에 생성되므로, 사용자가 늘어날 수록 통해 세션이 지속적으로 증가해서 문제가 생길 수 있음
  - 사용자가 서버에 최근 요청한 시간(`lastAccessedTime`)을 기준으로 30분 정도를 유지해주는 것이 대안이 될 수 있음
    - `HttpSession`은 이 방식을 사용한다
  - 설정
    - 글로벌 설정: `server.servlet.session.timeout`
    - 세션 단위로 설정: `session.setMaxInactiveInterval(1800)`

실무에서 주의할 점은 세션에는 최소한의 데이터를 보관해야한다는 점이다.
앞서 말햇듯이 보관한 데이터 용량 * 사용자 수로 메모리가 급격하게 늘어나서 장애로 이어질 수 있다.