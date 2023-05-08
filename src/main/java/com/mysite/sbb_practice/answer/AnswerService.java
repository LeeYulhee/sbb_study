package com.mysite.sbb_practice.answer;

import com.mysite.sbb_practice.DataNotFoundException;
import com.mysite.sbb_practice.question.Question;
import com.mysite.sbb_practice.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.mysite.sbb_practice.user.SiteUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
// final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Service
/* @Service : 데이터 처리를 위해 작성하는 클래스
              모듈화를 위해 사용(리포지터리를 사용해 기능을 구현하면 중복되는 경우가 많은데 서비스에 구현해두면 호출해서 사용할 수 있음
              보안(컨트롤러에는 리포지터리 없이 서비스를 통해서 DB에 접근하는 게 해킹 등을 대비해 보안 상 안전함)
              엔티티 객체와 DTO 객체의 변환(엔티티는 DB와 직접 맞닿아 있기 때문에 직접 사용하면 컬럼 변경 등의 문제가 생길 수 있어서 DTO(Data Transfer Object) 클래스가 필요 -> 서비스) */
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
    // Answer를 생성하는 메서드로 Question, 내용, SiteUser를 매개변수로 받음
        Answer answer = new Answer();
        // Answer 클래스의 answer 변수에 새로운 Answer 객체 생성해서 대입
        answer.setContent(content);
        // answer에 content로 들어온 String을 setContent 메서드를 이용해 content로 저장
        answer.setCreateDate(LocalDateTime.now());
        // answer에 현재 시간(LocalDateTime.now())을 setCreateDate 메서드를 이용해 crateDate로 저장
        answer.setQuestion(question);
        // answer에 question(answer를 단 질문)을 setQuestion 메서드를 통해 question으로 저장
        answer.setAuthor(author);
        // answer에 들어온 author(SiteUser 클래스를 참조)를 setAuthor 메서드를 통해 author로 저장
        this.answerRepository.save(answer);
        // 위의 과정을 통해 필요한 정보들을 객체에 넣어 주고 answerRepository에 save를 통해 answer 데이터를 저장
        return answer;
    }

    public Answer getAnswer(Integer id) {
    // Answer를 받아오는 메서드로 answer의 id를 매개변수로 받음
        Optional<Answer> answer = this.answerRepository.findById(id);
        // Answer 클래스를 참조한 answer 변수에 answerRepository에서 id로 찾은 answer를 대입하는데, Optional을 통해 해당 answer가 없는 경우를 대비
        if(answer.isPresent()) {
        /* 만약 answer의 값이 있다면(true)
           isPresent : Optional 객체가 값을 가지고 있다면 true, 없다면 false return */
            return answer.get();
            // answer를 가져와 return
        } else {
        // 그게 아니라면(answer의 값이 없다면)
            throw new DataNotFoundException("answer not found");
            // "answer not found" 메세지와 함께 DataNotFoundException 오류를 발생시킴
        }
    }

    public void modify(Answer answer, String content) {
    // answer를 수정하는 메서드로 answer(Controller에서 전달해줄 때 getAnswer로 넣은 뒤에 전달하기 때문에 값이 있음)와 content를 매개변수로 받음
        answer.setContent(content);
        // answer에 setContent를 통해 새로 들어온 content를 저장
        answer.setModifyDate(LocalDateTime.now());
        // answer에 setModifyDate를 통해 현재 시각을 수정 날짜로 저장
        this.answerRepository.save(answer);
        // 위에서 넣은 내용을 answerRepository에 save를 통해서 answer를 저장
    }

    public void delete(Answer answer) {
    // answer를 지우는 매서드로 answer(Controller에서 전달해줄 때 getAnswer로 넣은 뒤에 전달하기 때문에 값이 있음)를 매개변수로 받음
        this.answerRepository.delete(answer);
        // answerRepository에서 answer를 delete 메서드로 삭제
    }

    public void vote(Answer answer, SiteUser siteUser) {
    // 추천을 하면 추천한 사람이 answer의 voter로 저장되는 메서드로 answer와 siteUser를 매개변수로 받음
        answer.getVoter().add(siteUser);
        // answer의 getVoter()로 voter list를 불러오고 거기에 siteUser를 추가
        this.answerRepository.save(answer);
        // answer의 내용이 바뀌었으니 answerRepository에 answer를 저장
    }
}
