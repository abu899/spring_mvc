# Validation

## 클라이언트 검증 vs 서버 검증

- 클라이언트 검증은 조작할 수 있으므로 보안에 취약
- 서버만으로 검증하면, 즉각적인 고객 사용성이 떨어진다
- 두개를 적절히 섞어서 사용해야하지만, 마지막에 `서버 검증은 필수`

## V1

<p align="center"><img src="/img/1.png" width="80%"></p>


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

## 오류코드와 메시지 처리

에러 메시지를 일관성있게 관리하는 것이 목표!

## V3

- `FieldError`, `ObjectError`의 파라미터인 `codes`와 `argument`를 사용
  - `codes`: 메시지 코드를 배열로 전달, 순서대로 매칭해서 처음 매칭되는 메시지가 사용된다
  - `argument`: 메시지 내에 치환할 값을 전달한다
- 메시지와 국제화에 사용한 것과 같은 방법으로 진행하게 된다
  - `errors.properties`
- 이 또한 국제화 기능 사용가능하다!

## V4

`FieldError`, `ObjectError`는 사용하기 번거로우니 좀 더 자동화해서 사용할 수 있는 방법을 찾아보자

- `BindingResult`는 target이 되는 객체 바로 뒤에 온다.
  - 즉 `BindingResult`는 본인이 검증할 객체에 대해 이미 알고있다는 점을 인식하고 있자
  - `BindingResult`의 `getObjectName()`을 해보면 target의 이름이 출력됨
- `bindingReuslt`의 `rejectValue()`과 `reject()`을 활용해보자
  - `rejectValue`와 `reject`가 결국 `FieldError`, `ObjectError`를 생성해준다
  - 파라미터로 들어가는 `errorCode`는 메시지에 등록된 코드가 아니다. (`messageResolver`를 위한 에러 코드)

### 어떤식으로 오류코드를 설계할 것인가?

오류 코드는 범용적으로 만들거나 자세하게 만들 수 있다
```text
required=필수 값 입니다 // 범용
required.item.itemName= 상품 이름은 필수 값 입니다 // 상세
```
범용성이 오르면 세밀한 설명이 불가능하고, 상세 설명으로 하면 범용성이 떨어진다. 그렇다면 어떻게 하는게 좋을까?

- 범용적인 설명을 정의한다
- 상세적인 설명이 필요한 경우, 객체 이름과 필드 명을 조합한 단계를 둬서 이를 지원한다
- 하지만 우선순위는 상세 설명이 높고 범용적인 설명이 차순

**MessageCodesResolver**

- 검증 오류 코드로 메시지 코드를 생성한다
  - 즉, `errorCode`, `objectName`, `fieldName`을 토대로  message code들을 생성한다.
- 생성 규칙
  - 객체 오류
    1. errorCode + "." + object name -> (`required.item`)
    2. errorCode -> (`required`)
  - 필드 오류
    1. errorCode + "." + object name + "." + field -> (`required.item.itemName`)
    2. errorCode + "." + field -> (`required.itemName`)
    3. errorCode + "." + field type -> (`required.String`)
    4. errorCode -> (`required`)
- `BindingResult`의 `rejectValue`는 `MessageCodesResolver`를 통해 받은 message code들로 `FieldError` 및 `ObjectError`를 생성한다
  - `new FieldError("item", "quantity", item.getQuantity(), false, MessageCodesResolver.messageCodes, null)`
- 타임리프에서는 `th:error`를 통해 렌더링하고, 생성된 오류 메시지 코드를 순서대로 돌아가면서 메시지를 찾는다.

### 스프링이 직접 만든 오류메시지 처리

검증 오류 코드는 크게 두가지로 나눌 수 있다

1. 개발자가 직접 설정한 오류코드
   - `rejectValue`나 `reject`를 사용하여 직접 호출
2. 스프링이 직접 검증 오류에 추가한 경우
   - 주로 타입 정보가 맞지 않는 경우
   
2번과 같이 타입정보가 맞지 않는 경우 다음과 같은 4가지 메시지 코드가 `MessageCodesResolver`를 통해 생성된다

- `typeMismatch.item.price`
- `typeMismatch.price`
- `typeMismatch.java.lang.Integer`
- `typeMismatch`
- 위와 같이 스프링이 만든 `typeMismatch` 에러 코드들은 `defaultValues`에 의해 출력된다
  - `errors.properties`에 `typeMismatch`를 추가하면 내가 만든 에러 메시지를 넣을 수 있다

## V5

`Validator`를 `@ComponentScan`으로 스프링 빈으로 주입 후 기존 개발자가 개발한 코드를 분리한다. 
- `supports`: 해당 검증기를 지원하는 여부 확인
- `validate`: 실제 검증 코드
- 그런데 과연 반드시 `Validator`를 직접 실행해야할까?
  - `Validator`를 사용하면 스프링의 추가적인 도움을 받을 수 있다 (`WebDataBinder`)
  - `@InitBinder`를 통해 `WebDataBinder`를 파라미터로 받고 원하는 `Validator`를 넣어준다
  - 해당 컨트롤러 내에서는 내가 설정한 `Validator`가 `@Validated`가 있는 객체에 대해선 검증이 동작한다.
  - 글로벌 설정이 가능하나 `BeanValidation`이 자동으로 등록되지 않으므로, 직접 글로벌 설정을 하는 경우는 드믈다.

## Bean Validation

검증 로직을 매번 지금처럼 작성하는 것은 상당히 번거롭다. 
또한 일반적인 필드에 대한 검증 로직은 대부분 값이 있는지 없는지, 특정 범위에 들어오는지에 대한 경우가 다수를 차지한다.
이를 좀 더 간단하게 어노테이션으로 표현하는 게 `Bean Validation`

- `Bean Validation`은 특정한 구현체가 아닌, 검증을 위한 어노테이션과 여러 인터페이스의 모음
- `Bean Validation`은 java 표준으로 이를 구현한 구현체가 여러개 존재하며, 일반적으로 사용하는 구현체는 `Hibernate Validator`이다
- 스프링의 경우 `Bean Validator`를 직접 호출해서 사용하는 것이 아닌, 통합에 의해 사용할 수 있게 되어있다.
  - `@Validated`를 사용하면 자동으로 적용된다
- 스프링 부트는 자동으로 글로벌 validator로 등록한다
  - `LocalValidatorFactoryBean`을 글로벌 validator로 등록한다.
  - `@NotNull`같은 어노테이션을 보고 검증을 수행한다
  
### Bean Validation - Error Code

`Bean Validation`을 적용하고 오류 메시지를 변경하고 싶을 때!

- `bindingResult`에 등록된 검증 오류 코드를 보면, 오류 코드는 어노테이션 이름으로 등록된다
  - 마치 `typeMismatch`
  - `@NotBlank`의 경우 `NotBlank.item.itemName`, `NotBlank.itemName`, `NotBlank.java.lang.String`, `NotBlank`
- 따라서 메시지를 위의 규칙에 따라 등록하면 된다!
- `Bean Validation`이 메시지를 찾는 순서
  1. 생성된 메시지 코드 순서대로 `messageSource`에서 찾기
  2. 어노테이션의 `message` 속성 사용
  3. 라이브러리가 제공하는 기본 값 사용

### Bean Validation - Object Error

`Bean Validation`에서 필드에 어노테이션 형태로 들어가게 되는데 `Object Error`는 어떻게 처리될까?

- `@ScriptAssert`
  - `@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000")`
- 하지만 실제 사용해보면 제약이 많고 사용이 복잡하다
  - 실무에서는 검증 기능이 해당 객체의 범위를 넘어석는 경우가 존재하게 되면 대응이 어렵다
- 따라서 `ObjectError`의 경우 코드로 작성하는 것이 보다 간편.
