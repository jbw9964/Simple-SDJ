# Source code for practice

추후 프로젝트에 도움이 될 만한 부분 모음

---

## Application

- [로깅 aspect](./src/main/java/group/practice/configs/aspects/logging)
- [공통 에러 처리](./src/main/java/group/practice/exceptions)
- [공통 응답 처리](./src/main/java/group/practice/configs/aspects/response)

---

## Test

- 잡다한 utils
    - 간단한 assert 줄이기 용 : [`TestUtils`](./src/test/java/group/practice/TestUtils.java)
    - 컨트롤러 테스트시 에러 응답 생성 & `MockMvc` request builder 제공용 : [`MvcTestUtils`](./src/test/java/group/practice/MvcTestUtils.java)
- 추상 클래스로 Spring container 돌려쓰기
    - 통합 테스트용 : [`IntegrationTestSupport`](./src/test/java/group/practice/IntegrationTestSupport.java)
    - 컨트롤러 테스트용 : [`ControllerTestSupport`](./src/test/java/group/practice/ControllerTestSupport.java)

---

## 기타 TIP

- OSIV 는 끄자.
- 테스트 코드에 `@Transactional` 은 되도록 피하자.
- `Jackson` 의 `JsonNode` 를 이용하면 Controller 테코가 쉬워질 수 있다.
- `@ParameterizedTest` 는 유용하다. [예시](./src/test/java/group/practice/controllers/RegisterControllerTest.java)
- `logging.level.org.springframework.transaction=trace` 로 실제 transaction 이 걸리는지 볼 수 있다.
- 응답 코드가 204 (No content) 여도 body 는 존재한다. Postman 이 안보이게 처리하는 거일 뿐이다.
- `@EntityGraph` 는 그닥 유용하지 않아 보인다. 잘 사용하려면 `subGraph?` 등을 같이 이용해야 될 것 같다.

---

## 의문점

- OSIV 는 왜 씀?

흔히 트랜잭션 밖에서의 lazy loading 을 위해 킨다 카는데, 그건 그냥 설계 미스 아님? 당연히 DB 에서 불러올 때 고려해서 같이 가져와야 되는 거 아님?

- Facade 패턴은 왜 씀?

Facade 는 결국 서비스 layer 하나 더 추가하는 거 아님?
MSA 나 멀티모듈이면 이해하겠는데, 안 그런 시스템이 그렇게 복잡하면 migrate 하는게 현명한 거 아님?

- `@Transactional(readOnly = true)` 는 왜 씀?

저것도 엄연히 트랜잭션 아님?
동시성이 매우 높은 상황에서 이걸 사용하는 건 이해하겠는데, 그냥 단순히 "select 할 때도 사용해야 해요~" 는 전혀 이해 못하겠는데??

- 테코는 어디까지 짜야됨?

Controller 까지 만드는게 좋음? DAO 테코도 만드는게 좋음?
아님 그냥 상황 잘 생각해서 딱깔센으로 선택하는게 맞음? 가이드가 없네.

- 페이징 테코?

페이징 테코는 어떻게 만드는게 더 효율적임?
테코 더러워도 상관 없으니까 막 해도 됨?
아님 정말 간단하게만 만들어서라도 하는게 좋음? 모르겠네.

- 공통 응답 처리 + Swagger?

공통 응답 처리하면 실 응답이랑 Swagger 에서 보이는 게 좀 다를텐데, 어떻게 해결하는 방법 없음?

---
