# RequestMapping(요청 매핑)

## 매핑 정보 

[MappingController](../requestmapping/MappingController.java)

- @RestController
  - `@Controller`는 반환 값이 String이면 뷰 이름으로 인식하여 `뷰를 찾고 뷰가 렌더링` 된다
  - 하지만 `@RestController`는 반환 값으로 뷰가 아닌 `HTTP 메시지 바디에 바로 입력`한다.
- @RequestMapping
  - `@RequestMapping`에서 설정한 url이 들어오면 메서드를 실행하도록 매핑한다
  - 대부분 속성을 배열로 제공하므로 다중 설정 또한 가능하다
    - @RequestMapping({'/hello', '/hello2'})

> `/hello-basic`과 `/hello-basic/`은 다른 URL이지만 스프링은 같은 요청으로 매핑한다

### HTTP method

- `RequestMapping`에 method 속성으로 HTTP method를 지정하지 않으면 HTTP method와 무관하게 호출된다
  - 즉 GET, POST, PUT 등에 대해서도 동작해버린다.
- GetMapping, PostMapping 등을 사용하거나` RequestMapping(method = RequestMethod.GET)`를 설정해주자

### PathVariable

HTTP API가 리소스 경로에 식별자를 넣는 스타일이 선호되고 있다
- /mapping/userA
- /users/1
- 경로의 템플릿화
  - /mapping/{userId}
    - `@RequestMapping`은 경로를 템플릿화할 수 있는데, `@PathVariable`을 사용하면 이를 쉽게 가져올 수 있다
  - /mapping/{userId}/orders/{orderId}
    - 위와같이 여러 개의 경로가 템플릿화 되어도 사용 가능하다.

### 미디어 타입 조건 매핑

### Content-Type 헤더 기반 추가 매핑

- Content-Type의 헤더를 기반으로 미디어 타입으로 매핑한다
  - consumes
  - 클라이언트가 서버로 보내는 정보를 설정하는 것.
  - 조건이 맞지 않는다면, 415 code(Unsupported Media Type)을 반환한다
  
### Accept 헤더 기반 추가 매핑

- Accept 헤더를 기반으로 미디어 타입으로 매핑한다
  - produces
  - 클라이언트 입장에서 받아들일 수 있는 정보를 설정하는 것.
  - 조건이 맞지 않는다면, 406 code(Not Acceptable)을 반환한다

## API 예시 

[MappingClassController](../requestmapping/MappingClassController.java)

### 회원관리 API

- 회원 목록 조회
  - GET `/users`
- 회원 등록
  - POST `/users`
- 회원 조회
  - GET `/users/{userId}`
- 회원 수정
  - PATCH `/users/{userId}`
- 회원 삭제
  - DELETE `/users/{userId}`


