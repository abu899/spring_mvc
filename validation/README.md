# Validation

<p align="center"><img src="/img/1.png" width="80%"></p>

## V1

- 검증 에러를 저장하기 위해 Map에 에러와 에러메시지를 저장
- 실패하면 입력 폼으로 돌아가면서 model에 errors map을 넣어 뷰 템플릿에 보낸다
- 장점
  - 검증 오류가 발생해도 고객이 입력한 데이터가 유지
  - 검증 오류들을 고객에게 안내 및 재입력 가능
- 한계점
  - 뷰 템플릿에서 중복이 많이 존재한다
  - 타입오류 처리, 즉, 숫자 타입에 문자가 들어오면 오류가 발생한다
    - 위 오류는 스프링 MVC에서 컨트롤러에 진입하기 전에 예외가 발생하므로 400 예외가 발생하게 된다.
  - 만약 타입 오류가 발생하더라도, 서로 다른 타입의 데이터를 보관할 수 없어 고객이 입력한 문자가 사라짐

## V2

- `Thymeleaf`와 스프링의 `BindingResult` 사용
  - `(@ModelAttribute Item item, BindingResult bindingResult)` 순서가 중요하다
  -` @ModelAttribute` 객체의 결과가 `BindingResult`에 들어오기 때문에!
- `#fields`
  - `BindingResult`가 제공하는 검증오류에 접근
- `th:errors`
  - 해당 필드에 오류가 있는 경우 태그를 출력한다
  - `th:if`의 편의 버전
- `th:errorclass`
  - `th:field`에서 지정한 필드에 오류가 있는 경우 `class` 정보를 추가한다

### BindingResult

스프링이 제공하는 검증 오류를 보관하는 객체이다.

- `BindingResult`가 존재하면, `@ModelAttribute`에 데이터 바인딩시 오류가 발생해도 컨트롤러는 호출된다
  - V1에서 타입 오류시 컨트롤러가 호출되지 않는 부분 개선
  - 바인딩 실패로 문제가 되는 부분 또한 `BindingResult`의 `FieldError`에 담겨있게 된다.
- 그렇기 때문에 `BindingResult`는 순서가 중요하다!!!
- 엄밀히 말하면 `BindingResult` 입장에선 오류가 두 가지
  - `BindingResult` 앞 객체의 Binding 자체가 실패한 오류 (타입 오류)
  - 비지니스와 관련된 검증 오류 (우리가 개발한거)
- 한계점
  - 고객이 입력한 내용이 모두 사라진다

### FieldError, ObjectError

사용자가 잘못입력한 값도 유지되게 하는 것이 목표

- `FieldError`
  - `objectName`: 오류가 발생한 객체이름
  - `field`: 사용자가 입력한 값(거절된 값)
  - `bindingFailure`: 타입 오류같은 바인딩 실패인지, 검증 실패인지 구분 값
  - `codes`: 메시지 코드
  - `arguments`: 메시지에 사용하는 인자
  - `defaultMessage`: 기본 오류 메시지

> 바인딩 실패 시에는 스프링에서 `FieldError`를 만들어서 `BindingResult`에 넣어준다

- 타임리프의 사용자 입력 값 유지
  - `th:field="*{price}` 
  - `th:field`가 정상 동작에서는 모델 객체의 값을 사용하지만, 오류가 발생하면 `FieldError`에서 보관한 값을 사용한다!!