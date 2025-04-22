
# 1. 파라미터 바인딩 & 반환 타입

Spring data jpa 쿼리 명명 규칙 문서 : [Spring Data JPA - Query Creation](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.query-creation)

JPA 에서 JPQL 파라미터 바인딩은 아래처럼 진행한다.

```java
em.createQuery("""
                SELECT e FROM  TestEntity e WHERE e.id = :id
                """, TestEntity.class)
        .setParameter("id", 1)
        .getResultList();
```

<span style="color:#7898FB">**Spring data jpa 에서는 `@Query` 어노테이션으로 JPQL 을 정의하고 `@Param` 으로 바인딩을 진행한다.**</span>

```java
@Query("select e from TestEntity e where e.id = :id")
Optional<TestEntity> jpqlViaQuery(@Param("id") Long id);
```

또한 Spring data jpa 는 <span style="color:#7898FB">**`JpaRepository` 메서드 리턴 타입에 따라 SQL 쿼리 결과를 알아서 잘 맵핑해준다.**</span>

지원하는 타입과 각각에 대한 자세한 설명은 아래 링크에 설명되어 있다.

- [Repository query return types](https://docs.spring.io/spring-data/jpa/reference/repositories/query-return-types-reference.html)

---

# 2. 정렬과 페이징

## I. 정렬 : `Sort`

<span style="color:#7898FB">**Spring data 의 `Sort` 을 통해 쿼리 결과를 페이징 할 수 있다.**</span>

```java
@Query("select e from TestEntity e")
List<TestEntity> selectAll();

@Query("select e from TestEntity e")
List<TestEntity> selectAll(Sort sort);
```

이 때 특징은 JPQL 은 동일해도 `Sort` 매개변수를 넘겨 정렬할 수 있다는 점이다.
`Sort` 구현체는 아래처럼 생성해 사용할 수 있다.

```java
Sort byIdDesc = Sort.by(Sort.Direction.DESC, "id");
Sort byIdDescAndNameAsc = byIdDesc.and(Sort.by(Sort.Direction.ASC, "name"));

repo.selectAll(byIdDesc);
repo.selectAll(byIdDescAndNameAsc);
```

```
Hibernate:              // [byIdDesc]
    select
        te1_0.id,
        te1_0.bound,
        te1_0.name,
        te1_0.random_value 
    from
        test_entity te1_0 
    order by
        te1_0.id desc
Hibernate:              // [byIdDescAndNameAsc]
    select
        te1_0.id,
        te1_0.bound,
        te1_0.name,
        te1_0.random_value 
    from
        test_entity te1_0 
    order by
        te1_0.id desc,
        te1_0.name
```

참고로 위에서 사용한 `Sort.by` static 메서드는 아래와 같은 시그니처를 가지고, `reverse` 메서드도 제공해 매우 유연한 정렬이 가능하다.

```java
public static Sort by(Direction direction, String... properties) {/* ... */}
public Sort reverse() {/* ... */}
```

---

## II. 페이징 : `Pagable`

또한 <span style="color:#7898FB">**Spring data 는 `Pageable` 매개변수를 넘겨 손쉬운 페이징이 가능하다.**</span>

```java
@Query("select e from TestEntity e")
List<TestEntity> selectAll(Pageable pageable);
```

참고로 만약 `selectAll(Pageable pageable, Sort sort);` 와 같은 메서드는 다음 에러를 발생시킨다. (App 실행시 발생)

```
Failed to load ApplicationContext for ??? ...
...
Caused by: java.lang.IllegalStateException: Method method using Pageable parameter must not define Limit nor Sort. 
```

<span style="color:#7898FB">**즉, `Pageable` 과 `Sort` (+ `Limit` 인터페이스) 는 같이 사용될 수 없다는 것이다.**</span>

이는 페이징 자체에 정렬 정보가 포함되기 때문이다. 다음 `Pageable` 구현체 생성 예시를 보면 이해될 것이다.

```java
Pageable paging = PageRequest.of(0, 10);

Sort idDesc = Sort.by(Sort.Direction.DESC, "id");
Pageable pagingWithIdDesc = PageRequest.of(0, 10, idDesc);

repo.selectAll(pagingWithIdDesc);
``` 

```
Hibernate: 
    select
        te1_0.id,
        te1_0.bound,
        te1_0.name,
        te1_0.random_value 
    from
        test_entity te1_0 
    order by
        te1_0.id desc 
    fetch
        first ? rows only
```

위처럼 `PageRequest.of` 메서드에 정렬 정보를 제공하는 것을 볼 수 있다.

---

## III. 페이징 결과 : `Page` & `Slice`

페이징 메서드를 정의시 이전 예시처럼 `List<T>` 를 사용할 수도 있지만, <span style="color:#7898FB">**`Page<T>`, `Slice<T>` 를 통해 더 자세한 페이징 정보를 조회할 수 있다.**</span>

```java
@Query("select e from TestEntity e")
Slice<TestEntity> doPagingToSlice(Pageable pageable);

@Query("select e from TestEntity e")
Page<TestEntity> doPagingToPage(Pageable pageable);
```

<span style="color:#7898FB">**`Slice` 는 기본적으로 "현재 페이지 정보와 다음 조회 정보" 를 제공하기 _NoOffset 페이징_ (`ex: 무한 스크롤`)에 유리하다.** </span>

|             메서드             |           설명            |                    비고                     |
|:---------------------------:|:-----------------------:|:-----------------------------------------:|
|      `int getNumber()`      |  페이징 요청된 페이지 번호를 반환한다.  | `PageRequest.of(number, size)` 의 `number` |
|       `int getSize()`       | 페이징 요청된 페이지 사이즈를 반환한다.  |  `PageRequest.of(number, size)` 의 `size`  |
|   `List<T> getContent()`    |  페이징 요청으로 얻은 개체를 반환한다.  |           `List<TestEntity>` 등            |
| `int getNumberOfElements()` | 페이징 요청으로 얻은 개체 수를 반환한다. |        `getContent().size()` 와 동일         |
|     `boolean hasNext()`     |   다음 페이지가 존재하는지 반환한다.   |                                           |
|   `boolean hasPrevious()`   |   이전 페이지가 존재하는지 반환한다.   |                                           |
|     `boolean isFirst()`     |  해당 페이지가 첫 페이지인지 반환한다.  |        내부적으로 `!hasPrevious()` 로 동작        |
|     `boolean isLast()`      | 해당 페이지가 마지막 페이지인지 반환한다. |          내부적으로 `!hasNext()` 로 동작          |

반면 <span style="color:#7898FB">**`Page` 는 "전체 페이징 정보" 를 제공하기 때문에 게시글 형태의 페이징에 유리하다.**</span>
(+ `Page<T> extends Slice<T>` 여서 `Slice` 의 메서드를 사용할 수 있다. 중복된 메서드 설명 생략)

|            메서드            |      설명      |                       비고                        |
|:-------------------------:|:------------:|:-----------------------------------------------:|
| `long getTotalElements()` | 전체 개체 수를 반환  |       `select count(e) from entity` 와 동일        |
|   `int getTotalPages()`   | 전체 페이지 수를 반환 | `select count(e) from entity` 를 통해 계산된 전체 페이지 수 |

여기서 두가지 중요한 사실이 있다. 
첫째로 `Page` 는 "전체 페이징 정보" 가 포함되기 때문에 어쩔 수 없이 "전 개체 수" 를 탐색해야 한다.

때문에 <span style="color:#7898FB">**`Page` 타입으로 메서드를 구성하면 `select count(e) from entity` 와 같은 추가적인 쿼리가 발생한다.**</span>

```java
@Query(
        value = "select e from TestEntity e",
        countQuery = "select count(e) from TestEntity e"
)
Page<TestEntity> doPagingToPage(Pageable pageable);

repo.findAll(
        PageRequest.of(2, 10)
);
```

```
Hibernate: 
    select
        te1_0.id,
        te1_0.bound,
        te1_0.name,
        te1_0.random_value 
    from
        test_entity te1_0 
    offset
        ? rows 
    fetch
        first ? rows only
Hibernate: 
    select
        count(te1_0.id) 
    from
        test_entity te1_0
```

이 때 `@Query(countQuery = ...)` 를 통해 전체 개수를 집계할 때 사용할 JPQL 을 명시할 수 있다.

위 경우는 단순하지만 <span style="color:#7898FB">**`TestEntity` 에 여러 연관관계가 존재하면 count 시에도 join 이 일어날 수 있으므로 `countQuery` 를 명시하는게 좋다.**</span>

두번째는 사실 중요하진 않지만 유용한 정보이다.
`Slice`, `Page` 인터페이스를 보면 모두 `map(Function<...>)` 메서드가 존재한다.

```java
interface Page<T> extends Slice<T> {
    /* ... 생략 ... */
    
	/**
	 * Returns a new {@link Page} with the content of the current one mapped by the given {@link Function}.
	 *
	 * @param converter must not be {@literal null}.
	 * @return a new {@link Page} with the content of the current one mapped by the given {@link Function}.
	 * @since 1.10
	 */
	@Override
	<U> Page<U> map(Function<? super T, ? extends U> converter);
}

public interface Slice<T> extends Streamable<T> {
    /* ... 생략 ... */

    /**
     * Returns a new {@link Slice} with the content of the current one mapped by the given {@link Converter}.
     *
     * @param converter must not be {@literal null}.
     * @return a new {@link Slice} with the content of the current one mapped by the given {@link Converter}.
     * @since 1.10
     */
    @Override
    <U> Slice<U> map(Function<? super T, ? extends U> converter);
}
```

이는 페이징 결과 개체를 다른 것으로 `map` 하기 위한 메서드로, 엔티티를 DTO 로 변환하기 유용하다.

```java
record EntityDto(
        Long id,
        String name
) {
    public static EntityDto toDto(TestEntity entity) {
        return new EntityDto(entity.getId(), entity.getName());
    }
}

Page<TestEntity> toPage = repo.findAll(
        PageRequest.of(2, 10)
);

Page<EntityDto> contextHasBeenMapped = toPage.map(EntityDto::toDto);
```

---

## IV. `Controller` 에서 `Pageable` 받기

Spring Boot 는 다음처럼 쿼리 파라미터 (+ Request Body) 를 어느 개체로 바인딩할 수 있다.

```java
@GetMapping("/test")
public ResponseEntity<?> testEndpoint(
        TestRequestDto request
)   {
    return ResponseEntity.ok(request);
}
```

```json5
//  curl -X GET "http://localhost:8080/test?name=requested_name&age=10"
{
  "name": "requested_name",
  "age": 10
}
```

이를 활용해 다음처럼 <span style="color:#7898FB">**`Pagable` 을 Controller 매개변수로 활용할 수 있다.**</span>

관련 공식 문서 : [Spring Data Common - HandlerMethodArgumentResolvers for Pageable and Sort](https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.basic.paging-and-sorting)


```java
@GetMapping("/test-paging")
public ResponseEntity<?> testPaging(
        Pageable pagingRequest
) {

    Page<TestEntity> paging = repo.doPagingToPage(pagingRequest);
    Page<EntityInfoDto> response = paging.map(EntityInfoDto::of);

    return ResponseEntity.ok(response);
}
```

```json5
// curl -X GET http://localhost:8080/test-paging?page=2&size=3&sort=id,desc
{
  "content": [
    /* ... */
  ],
  "pageable": {
    "pageNumber": 2,
    "pageSize": 3,
    "sort": {
      "empty": false,
      "unsorted": false,
      "sorted": true
    },
    "offset": 6,
    "unpaged": false,
    "paged": true
  },
  "last": false,
  "totalPages": 34,
  "totalElements": 100,
  "first": false,
  "size": 3,
  "number": 2,
  "sort": {
    "empty": false,
    "unsorted": false,
    "sorted": true
  },
  "numberOfElements": 3,
  "empty": false
}
```

위 `CURL` 에서 볼 수 있듯, 페이징 요청은 아래와 같은 쿼리 파라미터가 필요하다.

| Query parameter key |   설명    |                        비고                         |
|:-------------------:|:-------:|:-------------------------------------------------:|
|       `page`        | 페이지 번호  |     `PageRequest.of(number, size)` 의 `number`     |
|       `size`        | 페이지 사이즈 |      `PageRequest.of(number, size)` 의 `size`      |
|       `sort`        |  정렬 정보  | `id,asc`, `id,randomValue,desc` 등 (`asc` 는 생략 가능) |

만일 해당 endpoint 에 쿼리 파라미터가 존재하지 않으면 `page=0`, `size=default-page-size` 로 해석되 페이징된다.

Paging 기본 값은 아래 `properties` 에서 global 하게 설정하거나 `@PageableDefault` 어노테이션을 활용해 설정할 수 있다.

```properties
spring.data.web.pageable.default-page-size=20
spring.data.web.pageable.max-page-size=2000
```

```java
@GetMapping("/test-paging")
public ResponseEntity<?> testPaging(
        @PageableDefault(page = 0, size = 10)
        Pageable pagingRequest
) {/* ... */}
```

참고로 `max-page-size` 보다 큰 페이지 사이즈가 전달될 경우, 이를 `max-page-size` 로 제한해 파라미터를 넘긴다.
(에러를 발생시키진 않는다는 뜻)

더불어 위 예시처럼 `Page<>` 를 그대로 response 할 경우 아래와 같은 warning 이 나올 수 있다.

```
WARN : Serializing PageImpl instances as-is is not supported, meaning that there is no guarantee about the stability of the resulting JSON structure!
	For a stable JSON structure, please use Spring Data's PagedModel (globally via @EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO))
	or Spring HATEOAS and Spring Data's PagedResourcesAssembler as documented in https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.pageables.
```

즉, `Page<>` 대신 `PagedModel<>` 을 response 하길 권장하고 있다. 이를 적용해 테스트해 보면 다음과 같다.

```java
@GetMapping("/test-paging")
public ResponseEntity<?> testPaging(
        @PageableDefault(page = 0, size = 10)
        Pageable pagingRequest
) {
    /* ... */
    Page<EntityInfoDto> response = paging.map(EntityInfoDto::of);
    PagedModel<EntityInfoDto> pagedModel 
            = new PagedModel<>(response);

    return ResponseEntity.ok(pagedModel);
}
```

```json5
// // curl -X GET http://localhost:8080/test-paging?page=2&size=3&sort=id,desc
{
  "content": [
    /* ... */
  ],
  "page": {
    "size": 3,
    "number": 2,
    "totalElements": 100,
    "totalPages": 34
  }
}
```

이전 `Page<>` 와 비교하면 `sort` 등의 속성이 생략된 것을 볼 수 있다.

더불어 사실 `PagedModel` 자체는 _Spring HATEOAS `PagedModel`_ 의 _"축약 버전"_ 이다.

HATEOAS 의 `PagedModel` 은 _"다음 페이징 요청 URL link"_ 가 존재하지만 Spring Data 의 `PagedModel` 은 그렇지 않기 때문이다. 
더 자세한 내용은 아래 공식 문서를 확인해보자.

- [Spring Data Commons - Using Spring Data' PagedModel](https://docs.spring.io/spring-data/commons/reference/repositories/core-extensions.html#core.web.page.paged-model)

---

# 3. 벌크성 수정 쿼리

JPA 에서 벌크성 쿼리는 영속 컨텍스트를 무시하고 실행되기 때문에 사용 전 `em.flush()`,사용 후 `em.clear()` 가 필요하다.

Spring data jpa 에서도 이는 마찬가지인데, 이를 위한 어노테이션이 <span style="color:#7898FB">**`@Modifying`**</span> 이다.

```java
@Modifying(
        flushAutomatically = true,
        clearAutomatically = true
)
@Query("update TestEntity e set e.name = :name")
int doBulk(String name);
```

만일 `@Modifying` 어노테이션이 없는 벌크성 쿼리를 실행하면 다음과 같은 에러가 발생한다.

```
Query executed via 'getResultList()' or 'getSingleResult()' must be a 'select' query [update TestEntity e set e.name = :name]
...
Caused by: org.hibernate.query.IllegalSelectQueryException: Expecting a SELECT Query, but found org.hibernate.query.sqm.tree.update.SqmUpdateStatement [update TestEntity e set e.name = :name]
	at org.hibernate.query.sqm.internal.SqmUtil.verifyIsSelectStatement(SqmUtil.java:140)
	...
```

`flushAutomatically` 는 벌크성 쿼리를 **실행 전** `em.flush()` 를 수행할 것인지를 나타내고, `clearAutomatically` 는 벌크성 쿼리 **실행 후** `em.clear()` 를 수행할 지를 나타낸다.
이들의 기본값은 모두 `false` 이다.

참고로 _DDL 구문_ 도 벌크성 쿼리로 분류되, `@Modifying` 이 필요한 JPQL 은 `UPDATE`, `INSERT`, `DELETE`, `DDL 구문` 이다.  

기본적으로 모든 JPQL 은 실행 직전 `em.flush()` 가 호출된다 알려져있다.
하지만 이는 절반만 맞는 내용으로, 엄밀히 말하면 (`FlushMode.AUTO` 일 때) **해당 JPQL 과 연관된 SQL 구문만 flush** 된다 말해야 한다.

때문에 <span style="color:#7898FB">**`flushAutomatically` 은 연관 SQL 뿐만 아니라 쓰기 지연 저장소의 모든 SQL 을 `flush` 함에 유의해야 한다.**</span>

잘 이해가 되지 않으면 아래 좋은 글이 있으니 참고하자.

- [[JPA] @Modifying의 flushAutomatically 옵션은 언제 쓰지? - 강병철's velog](https://velog.io/@gruzzimo/JPA-Modifying%EC%9D%98-flushAutomatically-%EC%98%B5%EC%85%98%EC%9D%80-%EC%96%B8%EC%A0%9C-%EC%93%B0%EC%A7%80)

---
