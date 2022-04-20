# 메시지와 국제화

## Message

다양한 메시지를 한 곳에서 관리하도록 하는 기능

- 특정 label의 단어를 하나의 파일에서 관리하여 전체 HTML에서 직접 고치는게 아니고 하나만 고치면 전체에 영향
- `message.properties`

## 국제화

메시지에서 설명한 메시지 설정파일을 나라별로 관리하면 서비스를 국제화할 수 있다

- `message_en.properties`
- `message_ko.properties`

## 스프링 메시지 소스설정

스프링이 제공하는 `MessageSource`를 스프링 빈으로 등록

- `MessageSoruce`는 interface
- 하지만 스프링 부트에서는 `MessageSource`를 자동으로 스프링 빈으로 등록
- 스프링 부트에서는 `application.properties`에서 메시지 소스를 설정할 수 있다

## 스프링 국제화 메시지 선택

스프링은 기본적으로 HTTP 메시지의 `Accept-Language`에 따라 지원하는 언어로 변경된다.  
만약 웹브라우저의 값과 상관없이 이를 바꾸려면 어떻게 해야할까 `LocaleResolver`

- 스프링 부트는 기본적으로 `AcceptHeaderLocaleResolver`를 사용한다
- 만약 Locale 선택 방식을 변경하려면, `LocaleResolver`의 구현체를 변경해서 쿠기나 세션 기반의 `Locale` 선택 기능을 사용할 수 있다
