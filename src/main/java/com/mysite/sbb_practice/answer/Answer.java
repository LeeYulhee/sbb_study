package com.mysite.sbb_practice.answer;

import com.mysite.sbb_practice.question.Question;
import com.mysite.sbb_practice.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
/* Entity : 데이터베이스 테이블과 매핑되는 자바 클래스로 @Entity 어노테이션을 사용해야 JPA가 Entity로 인식
            원래 Entity에는 Setter 미구현 권장 -> DB와 바로 연결되기 때문에 안전하지 않음
            보통 @Builder를 사용해서 해당 메서드를 Entity에 추가해 데이터 변경
   ORM : 데이터베이스에 데이터를 저장하는 테이블을 자바 클래스로 만들어 관리하는 기술
          데이터 베이스를 사용하려면 쿼리를 작성하는 과정이 필요한데, ORM(Object Relational Mapping)을 이용하면 자바 문법만으로 DB 사용 가능
          데이터를 관리하는데 사용하는 ORM 클래스 = Entity */
public class Answer {
    @Id
    // 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /* strategy : 고유 번호 생성하는 옵션
       GenerationType.IDENTITY : 해당 컬럼에 독립적인 시퀀스를 생성하여 번호를 증가시킬 때 사용
                                 == Auto_increment */
    private Integer id;

    @Column(columnDefinition = "TEXT")
    /* Column : 컬럼 명과 일치, 컬럼의 세부 설정을 위해 사용
                Entity의 속성은 @Column을 사용하지 않아도 테이블 컬럼으로 인식
                인식하고 싶지 않다면 @Transient 사용
                카멜 표기법이 DB 컬럼으로 전환될 때는 createDate -> create_date로 전환
       columnDefinition : 컬럼의 속성 정의 */
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    /* 관계 설정 : 엔티티에 서로 연결된 속성이라는 것을 명시해야 함
       관계 설정할 때 사용하는 애너테이션 : @ManyToMany(N:N관계) @ManyToOne(N:1관계) @OneToMany(1:N관계) 등 */
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;
}
