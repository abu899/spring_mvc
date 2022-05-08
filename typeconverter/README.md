# Type Converter

## Introduction

- HTTP 요청 파라미터는 모두 문자로 처리된다.
  - `HttpServletRequest`의 경우 요청 파라미터를 다른 타입으로 변환해야하는 경우 숫자타입으로 변환해야하는 경우가 자주 발생한다.
- 스프링이 제공하는 `@RequestParam`의 경우 따로 변환할 필요없이 int로 데이터를 받을 수 있다
  - 이 경우는 `스프링이 중간에서 타입을 변환해 주었기 때문`에 바로 사용가능한 것이다
  - 마찬가지로 `@ModelAttribute`, `@PathVariable`에서도 같은 타입 변환을 확인할 수 있다.
- 이때 사용되는 것이 `Type Converter`

스프링은 확장 가능한 컨버터 인터페이스를 제공한다. 즉 스프링에 추가적인, 개발자가 만든 타입으로의 타입변환이 필요하다면
이 컨버터 인터페이스를 구현해서 등록하는 것으로 타입 변환이 가능하다.

- 스프링은 용도에 따라 다양한 방식의 타입 컨버터를 제공한다
  - `Converter`: 기본타입 컨버터
  - `ConverterFactory`: 전체 클래스 계층 구조가 필요할 때
  - `GenericConverter`: 정교한 구현, 대상 필드의 어노테이션 정보 사용 가능
  - `ConditionalGenericConverter`: 특정 조건이 참인 경우에만 실행

## ConversionService

타입 컨버터 하나나를 직접 선언해서 사용하는 것은 불편하기에, 이를 묶어서 편리하게 사용할 수 있게 해주는 것

- `ConversionService`는 다음의 단순한 기능들만 제공한다
  - 개별 컨버터들을 등록하는 기능 
  - 컨버팅이 가능한지 확인 여부와 컨버팅 기능 제공
- In Spring
  - 스프링 내부에서는 `ConversionService`를 제공해주고 우리는 `WebMvcConfigurer`가 제공하는 `addFormatters()`를
  사용해서 추가하고자 하는 컨버터를 등록할 수 있다.

## Converter in View Template

타임리프의 경우 렌더링 시, 컨버터를 적용해서 렌더링하는 방법을 지원하여 객체를 문자로 변환하는 작업을 확인할 수 있다.

- 변수 표현식: `${...}`
- 컨버전 서비스 사용: `${{...}}`

폼에다가도 컨버젼을 적용할 수 있다.

- `th:field`는 `id`, `name`을 출력하는 기능에 추가적으로 컨버젼 서비스도 함께 적용된다

--- 

# Formatter

일반적인 웹 어플리케이션에서는 범용적인 타입 변환보다는 특정 포맷을 가진 변환을 하는 경우가 많다

- 숫자 1000을 문자 1000으로 변환하는데 1,000이라는 문자로 변환한다거나 1,000이라는 문자를 숫자 1000으로 변환
- 날짜 객체인 문자를 2022-05-08 21:03:11 과 같이 출력

## Converter vs Formatter

- `Converter`는 범용(객체에서 객체)
- `Formatter`는 문자에 특화(객체에서 문자, 문자에서 객체) + 현지화(`Locale`)
  - `Converter`의 특별한 버젼

## 스프링이 제공하는 기본 포맷터

포맷터는 기본 형식이 지정되어 있기 때문에, 객체의 각 필드마다 다른 형식으로 포맷을 지정하기는 쉽지 않다.
따라서 스프링에서는 어노테이션 기반으로 원하는 형식을 지정할 수 있는 포맷터 두가지를 제공한다.

1. `@NumberFormat`
   - 숫자 관련 형식 지정 포맷터 사용
   - `NubmerFormatAnnotationFormatterFactory`
2. `@DateTimeFormat`
   - 날짜 관련 형식 지정 포맷터 사용
   - `Jsr310DateTimeFormatAnnotationFormatterFactory`

---

# 주의 사항

`HttpMessageConverter`에는 `ConversionService`가 적용되지 않는다.

- 객체를 JSON으로 변환할 때 메시지 컨버터가 작동하는데, 메시지 컨버터는 HTTP 메시지 바디의 내용을 객체로 변환하거나 객체를 HTTP 메시지 바디에 입력하는 것!
- JSON을 객체로 변환하는 메시지 컨버터는 `Jackson` 라이브러리를 사용하기에 그 결과는 이 라이브러리에 의존된다
- 따라서 JSON 결과에 대한 포맷팅이나 변환은 해당 라이브러리가 제공하는 설정을 통해 지정해야한다.

`ConversionService`는 `@RequestParam`, `@ModelAttribute`, `@PathVariable`, 뷰 템플릿등 에서 사용 가능하다.