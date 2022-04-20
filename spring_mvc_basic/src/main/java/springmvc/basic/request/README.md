# Request

## 헤더 정보를 조회하는 방법

[Request Header](./RequestHeaderController.java)

- 어떤 값을 가져올 수 있는지 공식 문서를 참조하자
- Return value 또한 공식문서에서 지원하는 형태로 반환 가능하다! 
  - [Argument Official Site](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-arguments)

## 쿼리 파라미터, HTML form

클라이언트에서 서버로 요청데이터를 전달하는 방법은 다음 세가지 방법을 사용한다

- GET, 쿼리 파라미터
  - /url*?username=hello&age=20
- POST, HTML form
  - content-type:application/x-www-form-urlencoded
  - 메시지 바디에 쿼리 파라미터 형식으로 전달
- HTTP message body에 데이터를 직접 담아 요청
  - HTTP API에서 사용
    - JSON, XML, TEXT
    
스프링에서는 `@RequestParam`을 이용하면 편리하게 이를 가져올 수 있다

- [Request Param](RequestParamController.java)
  - HTTP 파라미터 이름이 변수이름과 같으면 `@RequestParam(name= "xx")` 생략 가능
  - String, int, Integer같은 단순 타입이면 `@RequestParam`도 생략 가능
- 파라미터 필수 여부(required)
  - `@RequestParam(required = false)` 처럼 필수 여부를 지정해서 반드시 들어와야하는 파라미터를 지정할 수 있다.
  - true가 default
- default value
  - 값이 들어오지 않으면 기본 값을 지정해준다
  - String의 경우 빈문자("")의 경우에도 default value로 설정해준다
- Map 형태로 param값 가져오기
  - `@RequestParam Map<String, Object>`를 이용해 map으로도 가져올 수 있다
  - MultiValueMap 또한 사용 가능
    - 하나의 키에 여러 개의 value를 가지는 Map

## Model Attribute

개발을 하게되면 요청 파라미터를 가져와서 객체를 만들고, 그 객체에 값을 넣어주는 과정을 거치게 된다.
```java
class test {
  public void getHelloData(
          @RequestParam String username,
          @RequestParam int age) {
    HelloData data = new HelloData();
    data.setUsername(username);
    data.setAge(age);
  }
}
```

하지만 `@ModelAttribute`를 사용하면 자동으로 객체를 만들어 값을 넣어준 후 파라미터로 전달해준다
- `@ModelAttribute`는 생략 가능하지만, `@RequestParam`도 생략 가능하여 혼선이 발생할 수 있으니 주의
- 다만 `Argument resolver`로 지정된 타입은 제외
```java
class test{
  public String modelAttributeV2(
          @ModelAttribute HelloData helloData){
    log.info("HelloData = {}", helloData);
    return "ok";
  }
}
```
순서는 다음과 같다
1. HelloData 객체를 생성
2. 요청 파라미터 이름으로 HelloData 객체의 property를 찾고 setter를 호출해서 값을 바인딩 해준다
   - 만약 property와 타입이 다르거나 하는 경우 binding error가 발생한다

> Property  
> getUsername, setUsername 메서드가 있으면, `username`이라는 프로퍼티를 가졌다고 본다.
> 값을 조회하려면 getUsername, 수정하려면 setUsername을 호출한다

## HTTP 요청 메시지

HTTP 메시지 바디에 데이터를 직접 담아서 요청하는 방법이다. 앞서 얘기한 HTTP API에서 주로 사용하는 방법이다.
메시지 바디에 직접 데이터가 넘어오는 경우, `@RequestParam`, `@ModelAttribute`를 사용할 수 없다.
- [Request Body String](RequestHeaderController.java)

### HttpEntity

- HTTP header, body 정보를 편리하게 조회 가능하다
- 요청 파라미터를 조회하는 기능과는 관련이 없다!!
  - 요청 파라미터는 말그대로 url이나 body에 쿼리 형식을 담는것이기에 완전 다른거!
- 응답에도 사용 가능하다
  - 메시지 바디 정보를 직접 반환한다
  - 헤더 정보 또한 포함이 가능하다
  - 뷰 정보를 조회하지 않는다
- `HttpEntity`를 상속받은 다음 객체들도 같은 기능을 제공한다
  - RequestEntity
    - HttpMethod(GET,POST..), url 정보가 추가 된다
    - 요청에서 사용 가능하다
  - ResponseEntity
    - HTTP 상태 코드 설정이 가능하다
    - 응답에서 사용 가능하다
    - `return new ResponseEntity<String>("hello", responseHeaders, HttpStatus.CREATED)`

> 스프링 MVC 내부에서 HTTP 메시지 바디를 읽어서 `문자나 객체`로 변환해서 전달해준다.
> 이때 `HTTP Message Converter`라는 기능이 사용된다.

### 요청 파라미터 vs HTTP 메시지 바디

- 요청 파라미터 조회 : @RequestParam, @ModelAttribute
- HTTP 메시지 바디 조회: @RequestBody

### RequestBody

- `RequestBody`에서 직접 만든 객체를 지정할 수 있다.
  - `@RequestBody HelloData helloData`
- `HttpEntity`나 `RequestBody`를 이용하면 `HTTP Message Converter`가 HTTP 메시지 바디의 내용을 우리가 원하는 문자나 객체등으로 변환해준다
  - `HTTP Message Converter`는 문자 뿐만 아니라 JSON 또한 객체로 변환해준다

### `@RequestBody`는 생략 불가능하다

- `RequestBody`를 생략하면 `@ModelAttribute`로 처리되어서 null값이 들어가버린다
- 스프링은 다음 어노테이션에 대해 다음과 같은 생략 규칙을 적용한다
  - @RequestParam
    - String, int, Integer 같은 단순 타입
  - @ModelAttribute
    - `argument resolver`로 지정해둔 타입외의 나머지

### ResponseBody

- `ResponseBody`에서도  해당 객체를 HTTP 메시지 바디에 직접 넣어줄 수 있다
- `HttpEntity` 또한 사용 가능하다

### 정리

- @RequestBody
  - 요청
  - JSON 요청 -> HTTP message converter -> 객체
- @ResponseBody
  - 응답
  - 객체 -> Http message converter -> JSON 응답