package com.mysite.sbb_practice.question;

import com.mysite.sbb_practice.answer.Answer;
import com.mysite.sbb_practice.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
// Entity : 데이터베이스 테이블과 매핑되는 자바 클래스로 @Entity 어노테이션을 사용해야 JPA가 Entity로 인식
public class Question {
    @Id
    // 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /* strategy : 고유 번호 생성하는 옵션
       GenerationType.IDENTITY : 해당 컬럼에 독립적인 시퀀스를 생성하여 번호를 증가시킬 때 사용
                                 == Auto_increment */
    private Integer id;

    @Column(length = 200)
    // Column : 컬럼 명과 일치, 컬럼의 세부 설정을 위해 사용 -> 길이를 200으로 제한
    private String subject;

    @Column(columnDefinition = "TEXT")
    // Column : 컬럼 명과 일치, 컬럼의 세부 설정을 위해 사용 -> 컬럼의 속성을 TEXT로 정의
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    /* 관계 설정 : Question 하나에 여러 개의 답변이 달릴 수 있기 때문에(1:N 구조) OneToMany로 정의
       mappedBy : 참조 엔티티의 속성명 -> Answer에서 Question이라고 선언했으니 question이라고 매핑
       cascade : OneToMany, ManyToOne 일 때(parent-child 관계), 엔티티의 상태가 변화하면 관계가 있는 엔티티에도 상태 변화를 전파시키는 옵션
       CascadeType.REMOVE : 질문 하나에 답변이 여러 개일 수 있는데, 질문을 삭제하면 답변들도 전부 삭제될 수 있게 작성한 속성(부모 엔티티가 삭제되면 자식 엔티티도 삭제되는 속성) */
    private List<Answer> answerList;
    // Answer이 여러 개일 수 있기 때문에 List로 받음

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;
    // 추천인이 여러 명일 수 있고, 중복되면 안되기 때문에 Set으로 받음
}
