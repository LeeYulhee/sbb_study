package com.mysite.sbb_practice.question;

import com.mysite.sbb_practice.DataNotFoundException;
import com.mysite.sbb_practice.answer.Answer;
import com.mysite.sbb_practice.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    private Specification<Question> search(String kw) {
    /* 검색어를 입력 받아 쿼리의 JOIN문과 WHERE문을 생성하여 return하는 메서드
       검색어를 입력으로 받아, Specification 인터페이스를 구현한 객체를 반환
       Specificaion은 JPA에서 제공하는 인터페이스로, 동적 쿼리를 만드는 데 사용됨
       동적 쿼리 : 실행 시점에 쿼리 조건이나 결과를 결정하는 쿼리. 즉, 검색 조검이나 정렬 기준 등이 프로그램 실행 중에 동적으로 변경될 수 있는 경우에 사용*/
        return new Specification<>() {
        // 익명 클래스를 사용하여 Specification 인터페이스를 구현한 객체를 생성
            private static final long serialVersionUID = 1L;
            /* serialVersionUID : 자바 직렬화 프로토콜에서 사용되는 고유 식별자
                                  Serializable 인터페이스를 구현한 클래스에서 사용됩니다. 이 인터페이스는 객체를 직렬화할 수 있도록 해주는데, 이때 serialVersionUID를 명시하면 클래스의 버전 정보를 명시적으로 지정할 수 있음
                                  직렬화된 객체를 역직렬화(deserialization)할 때, 클래스의 버전 정보를 확인하고 호환성 여부를 판단
                                  명시적으로 값을 지정하지 않으면 컴파일러가 자동으로 계산하여 부여
               직렬화 : 자바 객체를 이진 데이터(binary data)로 변환하는 과정을 말함. 이진 데이터로 변환된 객체는 파일이나 네트워크 전송 등을 통해 저장되거나 전송할 수 있음 */
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
            /* toPredicate : Specification 인터페이스에서 정의된 추상 메서드 중 하나로 JPA Criteria API를 사용하여 동적 쿼리를 생성하기 위해 구현해야 함
                             해당 메서드의 역할은 조건식(Predicate)를 생성하는 것으로, 조건식은 JPA Criteria API의 CriteriaQuery 인터페이스의 where() 메서드나 join() 메서드와 함께 사용
                             이 메서드는 Specification 인터페이스를 구현한 객체를 만들 때 반드시 구현해야 하는 메서드 중 하나이며, 동적 쿼리를 생성하기 위한 가장 중요한 메서드
               CriteriaQuery : JPA 쿼리를 나타내는 객체. 쿼리의 SELECT, FROM, WHERE, ORDER BY 등을 설정할 수 있음
               CriteriaBuilder : 쿼리의 WHERE 조건 등에서 사용되는 조건식을 만드는 데 사용되는 객체
               Root : 쿼리의 FROM 절에서 사용되는 엔티티의 루트(root) 객체. 루트 객체를 사용하여 엔티티의 속성에 접근할 수 있음
               => 이 매개변수들은 Specification 인터페이스에서 toPredicate() 메서드를 구현할 때 반드시 사용해야 하는 매개변수
                  메서드 내에서 동적으로 생성한 객체에 대한 매개변수 등도 사용할 수 있지만, 정해져 있는 것이 아니므로 사용하는 객체에 따라 다를 수 있음*/
                query.distinct(true);
                // 중복된 결과를 제거하도록 쿼리에 distinct를 추가
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                /* author 필드를 기준으로 Question 엔티티와 SiteUser 엔티티를 조인. LEFT 조인을 사용하므로, Question과 연관된 SiteUser가 없는 경우에도 결과가 반환됨
                   u1은 조인한 결과를 나타내는 객체 */
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                /* answerList 필드를 기준으로 Question 엔티티와 Answer 엔티티를 조인. LEFT 조인을 사용하므로, Question과 연관된 Answer가 없는 경우에도 결과가 반환됨
                   a는 조인한 결과를 나타내는 객체 */
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                /* author 필드를 기준으로 Answer 엔티티와 SiteUser 엔티티를 조인. LEFT 조인을 사용하므로, Answer와 연관된 SiteUser가 없는 경우에도 결과가 반환됨
                   u2는 조인한 결과를 나타내는 객체 */
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"),
                        cb.like(q.get("content"), "%" + kw + "%"),
                        cb.like(u1.get("username"), "%" + kw + "%"),
                        cb.like(a.get("content"), "%" + kw + "%"),
                        cb.like(u2.get("username"), "%" + kw + "%"));
                /* 검색어를 기반으로 다양한 조건을 or 연산자로 묶어서 반환
                   cb는 CritierBuider 객체로, JPA Critier API를 사용하여 쿼리를 생성할 때 사용
                   cb.like() 메서드로 각 필드에서 검색어를 포함하는 경우를 찾음 -> 각 조건은 or연산자로 묶여 있으므로, 하나 이상의 조건이 true인 경우에 해당하는 엔티티가 검색 결과로 반환 */
            }
        };
    }

    public Question getQuestion(Integer id) {
    // Question을 id로 검색한 결과를 얻는 메서드로, id를 매개변수로 받음
        Optional<Question> question = this.questionRepository.findById(id);
        // Question 클래스를 참조한 question 변수에 questionRepository에서 id로 찾은 question을 대입하는데, Optional을 통해 해당 question이 없는 경우를 대비
        if (question.isPresent()) {
        // 만약 question의 값이 있다면(true)
            return  question.get();
            // question을 가져와 return
        } else {
        // 그게 아니라면(question의 값이 없다면)
            throw new DataNotFoundException("question not found");
            // "question not found" 메세지와 함께 DataNotFoundException 오류를 발생시킴
        }
    }

    public void create(String subject, String content, SiteUser user) {
    // Question을 생성하는 메서드로, subject(제목), content(내용), user(글 쓴 사람/사용자)을 매개변수로 받음
        Question q = new Question();
        // Question 클래스의 q 변수에 새로운 Question 객체 생성해서 대입
        q.setSubject(subject);
        // q에 subject로 들어온 String을 setSubject 메서드를 이용해 subject로 저장
        q.setContent(content);
        // q에 content로 들어온 String을 setContent 메서드를 이용해 content로 저장
        q.setCreateDate(LocalDateTime.now());
        // q에 현재 시간(LocalDateTime.now())을 setCreateDate 메서드를 이용해 createDate로 저장
        q.setAuthor(user);
        // q에 들어온 user(SiteUser 클래스를 참조)를 setAuthor 메서드를 통해 author로 저장
        this.questionRepository.save(q);
        // 위의 과정을 통해 필요한 정보들을 객체에 넣어주고 questionRepository에 save를 통해 q 데이터를 저장
    }

    public Page<Question> getList(int page, String kw) {
    // Page 클래스를 이용해 Question을 페이징 처리해서 받아오는 메서드로 page와 kw(검색어)를 매개변수로 받음
        List<Sort.Order> sorts = new ArrayList<>();
        // Sort.Order를 참조한 List인 sorts 변수에 ArrayList 객체를 생성에서 대입
        sorts.add(Sort.Order.desc("createDate"));
        // sorts.add로 정렬 조건을 추가 -> createDate(작성일자)의 역순(desc)으로 정렬하는 조건 추가
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        /* Pageable 클래스를 참조한 pageable 변수에 PageRequest.of 메서드를 사용하여 page(페이지 번호), 10(한 번에 띄우는 question 개수), Sort.by(sorts)(작성일자의 내림차순으로 정렬한 list)의 결과를 생성해서 대입
           Pageable : 해당 인터페이스는 페이지네이션에 필요한 정보를 담음 */
        Specification<Question> spec = search(kw);
        // Question을 참조한 Specification(Specificaion은 동적 쿼리를 만드는 데 사용됨)의 변수 spec에 kw(검색어)로 들어온 값을 search 메서드(검색어를 입력 받아 쿼리의 JOIN문과 WHERE문을 생성)에 매개변수로 전달한 값을 대입
        return this.questionRepository.findAllByKeyword(kw, pageable);
        // questionRepository의 findAllByKeyword 메서드를 이용해 kw로 전달된 값으로 검색한 결과를 pageable을 이용해 조건에 맞게 페이징 처리해서 return
    }

    public void modify(Question question, String subject, String content) {
    // question을 수정하는 메서드로 question(Controller에서 전달해줄 때 getQuestion으로 넣은 뒤에 전달하기 때문에 값이 있음)과 subject, content를 매개변수로 받음
        question.setSubject(subject);
        // question에 setSubject를 통해 새로 들어온 subject를 저장
        question.setContent(content);
        // question에 setContent를 통해 새로 들어온 content를 저장
        question.setModifyDate(LocalDateTime.now());
        // question에 setModifyDate를 통해 현재 시각을 수정 날짜로 저장
        this.questionRepository.save(question);
        // 위에서 넣은 내용을 quetionRepository에 save를 통해서 question을 저장
    }

    public void delete(Question question) {
    // question을 지우는 메서드로 question(Controller에서 전달해줄 때 getQuestion으로 넣은 뒤에 전달하기 때문에 값이 있음)을 매개변수로 받음
        this.questionRepository.delete(question);
        // questionRepository에서 question을 delete 메서드로 삭제
    }

    public void vote(Question question, SiteUser siteUser) {
    // 추천을 하면 추천한 사람이 question의 voter로 저장되는 메서드로 question과 siteUser를 매개변수로 받음
        question.getVoter().add(siteUser);
        // question의 getVoter()로 voter list를 불러오고 거기에 siteUser를 추가
        this.questionRepository.save(question);
        // question의 내용이 바뀌었으니 questionRepository에 question을 저장
    }
}
