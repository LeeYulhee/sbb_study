package com.mysite.sbb_practice.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);

    @Query("select "
    /* @Query : 쿼리가 익숙하다면 직접 쿼리를 작성하는 방법도 있음 -> Query 애너테이션 사용
                쿼리를 작성할 때에는 반드시 테이블 기준이 아닌, 엔티티 기준으로 작성해야 함(속성명이나 테이블명을 엔티티명으로 사용)
                쿼리에 파라미터로 전달할 kw 문자열은 메서드의 매개변수 @Param("kw")처럼 @Param 애너테이션을 사용해야 함
                검색어를 의미하는 kw는 @Query 안에서 :kw로 참조
       @Param : 데이터베이스에 다수의 변수를 전달할 때, 전달되는 변수들에 @Param 애너테이션을 붙여주어 각 변수를 구분할 수 있도록 함*/
            + "distinct q "
            + "from Question q "
            + "left outer join SiteUser u1 on q.author=u1 "
            + "left outer join Answer a on a.question=q "
            + "left outer join SiteUser u2 on a.author=u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
    /* JPA를 사용하여 데이터베이스에 Question 엔티티를 kw(키워드)로 검색하고, 페이징 처리된 결과를 반환하는 메서드
       @Param("kw") : 메서드 매개변수에 주어진 "kw"라는 이름의 파라미커를 지정(kw 파라미터는 String 타입)
       Pageable pageable : Pageable 인터페이스를 구현한 객체를 매개변수로 받음. 이 객체는 페이징 정보를 포함하고 있으며, 결과를 페이지 단위로 나누어 반환할 수 있도록 함
       findAllByKeyword : JPA에서 지원하는 메서드 이름 규칙을 따라, findBy 다음에 Keyword가 붙어서 메서드 이름이 점해짐. 이 메서드는 Keyword로 검색하는 모든 Question 엔티티를 찾음
       Page<Question> : 반환 타입은 Page <Question>으로, 검색된 결과를 페이징 처리한 후 Question 엔티티의 페이지를 반환 */
}
