# File upload

## HTML Form의 두가지 전송 방식

1. application/x-www-form-urlencoded
   - HTML 폼 데이터를 서버로 전송하는 가장 기본적인 방법
   - 헤더에 `Content-Type: application/x-www-form-urlencoded`가 추가된다
   - 폼에 입력한 전송할 항목을 HTTP Body에 문자로 `username=kim&age=20`와 같이 `&`로 구분해서 전송한다

2. multipart/form-data
   <p align="center"><img src="/img/1.png" width="80%"></p>

   - 파일을 업로드 하려면 파일은 문자가 아니라 `바이너리 데이터`를 전송해야 한다
   - `multipart/form-data`는 문자와 바이너리를 동시에 전송할 수 있는 방식을 제공한다
   - `ContentDisposition`이라는 항목별 헤더가 추가되어 있고 여기에 부가 정보가 존재한다
   - 파일의 경우 `파일 이름과 Content-Type`이 추가되고 바이너리 데이터가 전송된다
   - 각각의 부분이 `Part`라는 이름으로 나누어져 있다

## Servlet and File upload

### 멀티파트 사용 옵션

```text
spring.servlet.multipart.max-file-size=1MB
spring.servlet.multipart.max-request-size=10MB
```
- max-file-size: 파일당 최대 크기
- max-request-size: 멀티파트 요청된 파일의 총 크기

## Spring File upload

스프링은 `MultiPartFile`을 지원해 멀티파트를 보다 간단히 사용할 수 있다

- `@RequestParam MultipartFile file`
  - 업로드하는 Form의 이름에 맞춰 `@RequestParam`을 적용하면 된다