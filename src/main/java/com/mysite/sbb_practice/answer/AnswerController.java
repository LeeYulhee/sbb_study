package com.mysite.sbb_practice.answer;

import com.mysite.sbb_practice.question.Question;
import com.mysite.sbb_practice.question.QuestionService;
import com.mysite.sbb_practice.user.SiteUser;
import com.mysite.sbb_practice.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/answer")
/* 중복되는 URL 부분을 해당 애너테이션에 넣은 후 Mapping 부분에서 생략할 수 있음
   다만, 설정하면 해당 클래스의 URL  매핑은 항상 애너테이션에 넣은 부분으로 시작해야 함 */
@RequiredArgsConstructor
// final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
// MVC에서 C에 해당하며, 주로 사용자의 요청을 처리한 후 지정된 뷰에 모델 객체를 넘겨주는 역할
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    /* @PreAuthorize : 메서드 호출 전에 검사
       "isAuthenticated()" : 요청에 대해 인증된 사용자만 접근할 수 있도록 설정하는 메서드 -> 로그인한 상태 */
    @PostMapping("/create/{id}")
    /* @PostMapping : 매핑을 담당하는 역할로, POST 요청만 받아들일 경우에 사용
                      POST 요청은 서버의 리소스를 새로 생성하거나 업데이트할 때 사용(DB로 따지면 Create에 가깝고, 멱등X(리소스를 생성/업데이트에 사용해서 POST 요청 발생 시 서버가 변경될 수 있음))
                      POST는 리소스를 생성/변경하기 위해 설계되었기 때문에, HTTP 메세지의 Body에 담아 전송
                      POST 요청은 캐시되지 않고, 브라우저 히스토리에 남지 않으며, 데이터 길이에 제한이 없음
                      메서드의 매개변수 이름과 템플릿의 항목 이름이 같아야 함
                      @GetMapping과 매개변수가 다른 경우 같은 메서드 이름을 사용할 수 있음(메서드 오버로딩)
                      보안 등을 생각한다면 조회 빼고는 POST 처리하는 게 좋을 수도 있음
                      GET과 POST를 제외한 요청은 HTML에서 적용이 안 됨 -> hiddenmethod를 키면 실제는 POST지만 요청을 이식해서 자동 변환(PATCH : 수정, DELETE : 삭제) */
    /* "/create/{id}" : question_detail.html의 th:action="@{|/answer/create/${question.id}|}" 부분에서 question 객체의 id를 받아와 URL이 생성됨
                        -> answer를 작성하기 전에 접속한 페이지에 question 객체의 정보가 있음 */
    public String creatAnswer(Model model, @PathVariable("id") Integer id, @Valid AnswerForm answerForm, BindingResult bindingResult, Principal principal) {
        /* Model : MVC의 M으로 컨트롤러에서 데이터를 생성해 이를 View에 전달하는 역할(addAttribute 메서드로 모델에 속성과 값을 추가해서 전달할 수 있음)
                   따로 객체를 생성할 필요 없이 컨트롤러 메서드의 매개변수로 지정하면 스프링부트가 자동으로 객체 생성 */
        /* @Valid : 매개변수 앞에 @Valid 애너테애션을 적용해야 Form 클래스에 애너테이션으로 설정한 검증 기능이 작동(바인딩)
           @PathVariable : 변하는 값을 얻을 때 사용하는데, 매개변수와 Mapping URL 쪽에서 사용한 이름이 동일해야 함. URL 경로에서 값을 추출하여 메서드의 인자로 전달할 때 사용
           BindingResult : 항상 @Valid 뒤에 위치해야 하며, 애너테이션으로 인해 검증이 수행된 결과를 의미하는 객체
           Principal : SpringSecurity가 제공하는 Principal 객체를 사용해 로그인한 사용자에 대한 정보를 알 수 있음 */
        Question question = this.questionService.getQuestion(id);
        // Question 클래스를 참조한 question 변수에 questionService의 getQuestion(id) 메서드의 결과값(id로 questionRepository에 검색한 결과)을 대입(Question을 대입)
        SiteUser siteUser = this.userService.getUser(principal.getName());
        // StieUser 클래스를 참조한 siteUser 변수에 userService의 getUser() 메서드를 이용해 현재 로그인한 사용자의 이름으로 검색한 user 정보를 얻어와 대입
        if (bindingResult.hasErrors()) {
            // From을 검사한 결과에 만약 에러가 있다면
            model.addAttribute("question", question);
            // model 객체에 "question" 속성과 question 객체를 값으로 전달(View에 전달하는 과정, 속성은 데이터 이름으로 이 이름을 이용하여 View에서 데이터 참조)
            return "question_detail";
            // "question_detail.html" 템플릿(View)를 return == 다시 answer/create/{question.id}로 돌아감
        }
        Answer answer = this.answerService.create(question, answerForm.getContent(), siteUser);
        /* Answer 클래스를 참조한 answer 변수에 answerService의 create 메서드를 실행한 결과 값을 대입
           -> create 메서드에서는 Answer 객체를 새로 생성해 question, (Form에서 검증한) content와 author(==siteUser)를 값으로 저장 */
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
        // redirect로 해당 URL로 이동되는데 question/detail/(question.id) -> 질문 페이지, #answer_(answer.id) -> 앵커 기능으로 해당 질문 페이지 내의 답변 목록 중 (answer.id)의 위치로 스크롤 이동
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @GetMapping("/modify/{id}")
    // "answer/modify/(answer.id)"와 URL 연결
    /* @GET 요청 : GET은 서버의 리소스에서 데이터를 요청할 때 사용(DB로 따지면 GET은 SELECT에 가깝고, 멱등(연산을 여러 번 적용해도 결과가 달라지지 않음 = 리소스를 조회하는 것이라))
                   GET은 요청을 전송할 때, 힐요한 데이터를 Body에 담지 않고, 쿼리스트링(URL 끝에 ?와 함께 이름과 앖으로 쌍울 이루는 요청 파라미터)을 통해 전송
                   GET 요청은 캐시가 가능(GET을 통해 서버에 리소스를 요청할 때, 웹 캐시가 요청을 가로채 리소스를 다시 다운로드 하는 대신 리소스의 복사본 반환)함
                   GET 요청은 브라우저 히스토리에 남으며, 길이에 제한이 있고, 보안 상의 문제로 중요한 정보를 다루지 않는 것이 좋음*/
    public String answerModify(AnswerForm answerForm, @PathVariable("id") Integer id, Principal principal) {
        /* answer를 수정할 때 사용하는 메서드(GET이라 정보를 얻어오는 역할)로, 세 가지 매개변수를 받음
           -> AnswerForm(답변 수정 시, 기존의 내용이 필요하므로 AnswerForm 객체에 조회한 값을 메서드 내부에서 저장할 예정)과 answer의 id, 현재 로그인한 사용자(principal)를 매개변수로 받음
           AnswerForm은 현재 비어 있는 상태(Null)로, 기존 답변을 저장 -> View에 전달할 예정이기 때문에 선언한 것으로 Spring MVC에서 Form 객체로 자동 생성해 준 것
           Form : 데이터를 담는 객체 */
        Answer answer = this.answerService.getAnswer(id);
        // Answer 클래스를 참조한 answer 변수에 answerService에서 id를 getAnswer에 넣어 answerRepository에 findById로 조회된 Answer를 대입
        if(!answer.getAuthor().getUsername().equals(principal.getName())) {
        // 만약 answer의 author(SiteUser)로 username을 받아와 해당 값이 현재 사용자(principal)의 이름과 같지 않다면
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            /* "수정 권한이 없습니다"라는 메세지와 함께 오류 반환
               ResponseStatusException(HttpStatus.BAD_REQUEST) : 400 Bad Request HTTP 응답코드로 반환 */
        }
        answerForm.setContent(answer.getContent());
        // 비어 있는 answerForm에 기존(현재) answer의 내용을 setContent로 저장
        return "answer_form";
        // answer_form.html(answer 수정 화면) return
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @PostMapping("/modify/{id}")
    // "answer/modify/(answer.id)"와 URL 연결
    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult, @PathVariable("id") Integer id, Principal principal) {
        // answer를 수정하는 메서드 : Form에 입력된 내용을 받아서 @Valid로 Form 클래스에 애너테이션으로 설정한 검증 기능 실행(바인딩)한 뒤 해당 결과를 BindingResult로 받고, answer의 id와 현재 사용자 정보(principal)를 매개 변수로 받음
        if(bindingResult.hasErrors()) {
        // 만약 bindingResult(answerForm 내용을 검증한 결과)에 Error가 있다면
            return "answer_form";
            // 다시 answer_from.html(answer 수정하는 화면)으로 이동
        }
        Answer answer = this.answerService.getAnswer(id);
        // Answer 클래스의 answer 변수에 answerService에서 현재 answer의 id 값으로 answerRepository에서 조회한 값을 대입
        if(!answer.getAuthor().getUsername().equals(principal.getName())) {
        // 만약 answer의 작성자의 username이 현재 사용자(principal)의 이름과 같지 않다면
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            // "수정 권한이 없습니다"라는 메세지와 함께 오류 반환
        }
        this.answerService.modify(answer, answerForm.getContent());
        /* answerService의 modify메서드에 answer와 새로 들어온 answerForm의 내용을 매개변수로 넣고 실행
           answerService.modify : answer 객체의 content(내용)에 매개변수로 들어온 내용을 넣고, 현재 날짜를 modifyDate에 넣은 뒤, 해당 객체를 answerRepository에 save로 저장 */
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
        // redirect로 해당 URL로 이동되는데 question/detail/(question.id) -> 질문 페이지, #answer_(answer.id) -> 앵커 기능으로 해당 질문 페이지 내의 답변 목록 중 (answer.id)의 위치로 스크롤 이동
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @GetMapping("/delete/{id}")
    // "answer/delete/(answer.id)"와 URL 연결
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        // answer를 삭제하는 메서드 : 현재 사용자 정보(principal)와 answer의 id가 매개변수로(@PathVariable을 통해) 들어옴
        Answer answer = this.answerService.getAnswer(id);
        // Answer 클래스의 answer 변수에 answerService에서 현재 answer의 id 값으로 answerRepository에서 조회한 값을 대입
        if(!answer.getAuthor().getUsername().equals(principal.getName())) {
        // 만약 answer의 작성자의 username이 현재 사용자(principal)의 이름과 같지 않다면
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
            // "수정 권한이 없습니다"라는 메세지와 함께 오류 반환
        }
        this.answerService.delete(answer);
        // answerService의 delete 메서드를 통해 answerRepository에서 해당 answer를 삭제
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
        // 완료 후 answer에서 얻어 온 question을 통해 question의 id를 사용해서 question/detail/(question.id)로 이동(해당 질문 페이지)
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @GetMapping("/vote/{id}")
    // "answer/vote/(answer.id)"와 URL 연결
    public String answerVote(Principal principal, @PathVariable("id") Integer id) {
        // answer를 추천하는 메서드 : 현재 사용자 정보(principal)와 answer의 id가 매개변수로(@PathVariable을 통해) 들어옴
        Answer answer = this.answerService.getAnswer(id);
        // Answer 클래스의 answer 변수에 answerService에서 현재 answer의 id 값으로 answerRepository에서 조회한 값을 대입
        SiteUser siteUser = this.userService.getUser(principal.getName());
        // SiteUser 클래스의 siteUser 변수에 userService에서 현재 사용자 정보(principal)의 이름으로 userRepository에서 조회한 값을 대입
        this.answerService.vote(answer, siteUser);
        /* answerService의 vote 메서드에 answer와 siteUser를 매개변수로 넣음 -> vote 메서드에서 answer의 voter Set(중복X)을 불러와서 현재 siteUser를 add하고 answerRepository에 정보가 바뀐 answer를 save
           추천 숫자가 올라가는 곳은 question_detail.html에 th:text="${#lists.size(answer.voter)}"로 구현되어 있음 */
        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
        // redirect로 해당 URL로 이동되는데 question/detail/(question.id) -> 질문 페이지, #answer_(answer.id) -> 앵커 기능으로 해당 질문 페이지 내의 답변 목록 중 (answer.id)의 위치로 스크롤 이동
    }
}