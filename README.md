# 구글 reCaptcha V3 테스트

## 개요
    HTML 에 구글 reCaptcha 기능 사용여부를 확인하기 위한 테스트 프로젝트

### 실행 방법
    1. 스프링부트 실행 후 localhost:8080 호출
    2. id/pw 입력 후 제출

### 기능 설명

+ 인터넷 사용 가능
    + reCaptcha의 점수산정 방식으로 봇 여부를 판단
+ 인터넷 사용 불가
    + SimpleCaptcha 라이브러리를 사용해 랜덤한 이미지를 생성
    + 생성된 이미지의 6개 숫자를 입력받아 검사 

### 결함
+ SimpleCaptcha 라이브러리의 경우 jdk 1.6을 기준으로 만들어져 특정 class 가 동작하지 않음
  + jdk 1.8버전 이상을 사용하는 환경에서 이미지는 잘 생성되나, NullPointerException 이 발생함


### TODO
+ 테스트케이스 작성
    