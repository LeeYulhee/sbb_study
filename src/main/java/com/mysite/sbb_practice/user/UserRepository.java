package com.mysite.sbb_practice.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/* Entity만으로는 DB에 데이터를 저장하거나 조회할 수 없음 -> 데이터 처리르 루이해서는 실제 DB와 연동하는 JPA 리포지터리 필요
   Repository : 엔티티에 의해 생성된 DB 테이블에 접근하는 메서드들을 사용하기 위한 인터페이스로, CRUD를 어떻게 처리할지 정의하는 계층
   JPA Repository를 상속할 때는 제네릭 타입으로 설정 : Repository의 대상이 되는 Entity 타입, 해당 Entity의 PK 속성 타입을 지정
 */
public interface UserRepository extends JpaRepository<SiteUser, Long> {
    Optional<SiteUser> findByusername(String username);
}