package com.mysite.sbb_practice.question;

import com.mysite.sbb_practice.answer.AnswerForm;
import com.mysite.sbb_practice.user.SiteUser;
import com.mysite.sbb_practice.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
// 중복되는 URL 부분을 해당 애너테이션에 넣은 후 Mapping 부분에서 생략할 수 있음
@RequiredArgsConstructor
// final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
// MVC에서 C에 해당하며, 주로 사용자의 요청을 처리한 후 지정된 뷰에 모델 객체를 넘겨주는 역할
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")
    // "question/list"와 URL 연결
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        /* 질문 목록이 있는 list 페이지를 보여주는 메서드로, Model 객체와 page(페이징 처리를 위한 매개변수), kw(검색을 위한 매개변수)를 매개 변수로 받음
           Model : Model : MVC의 M으로 컨트롤러에서 데이터를 생성해 이를 View에 전달하는 역할(addAttribute 메서드로 모델에 속성과 값을 추가해서 전달할 수 있음)
                   따로 객체를 생성할 필요 없이 컨트롤러 메서드의 매개변수로 지정하면 스프링부트가 자동으로 객체 생성
           @RequestParam : URL 파라미터로 전달 받은 value를 메서드의 파리마터로 받을 수 있게 해주는 애너테이션
                           HTTP 요청 파라미터를 메서드의 인자로 전달할 때 사용
                           @RequestParam은 HTTP 요청 파라미터를 컨트롤러 메서드의 매개변수에 바인딩하는데 사용
                           주로 GET 및 POST 요청에서 쿼리 파라미터나 폼 데이터를 처리할 때 사용
                           @RequestParam은 요청 파라미터를 사용하여 데이터를 받음
                           예시 : http://localhost:8080/example?name=test 요청이 들어오면, name 파라미터(test)가 메서드 매개변수에 바인딩
                           (@PathVariable은 http://localhost:8080/example/123 요청이 들어오면, id 경로 변수(123)가 메서드 매개변수에 바인딩)
           value = "page", defaultValue = "0" : URL에 페이지 파라미터 page가 전달되지 않은 경우 디폴트 값이 0으로 설정
           value = "kw", defaultValue = "" : URL 페이지에 파라미터 kw가 전될되지 않은 경우 디폴트 값이 ""로 설정 -> 모든 값을 검색한다는 뜻 == 모든 목록 출력 */
        /* 실행 흐름 : Controller에 매핑된 URL로 요청이 들어오면, questionService의 getList 메서드 실행 -> questionService의 getList는 int page를 받아 PageRequest.of(page, 개수)를 Pageable 객체에 넣음
                       -> questionService는 questionRepository에서 pageable(page, 개수/페이징 처리)이 적용된 findAll을 return -> Page<Question> 객체에 questionService.getList한 값이 들어감
                       -> model.addAttribute로 paging 객체를 "paging" 속성으로 전달 -> Controller는 해당 값을 전달 받고 처리된 question_list.html(View) return */
        Page<Question> paging = this.questionService.getList(page, kw);
        // Question 클래스를 Page 클래스(페이징 처리를 위한 클래스)로 받은 데이터를 참조한 paging 변수에 questionService에서 현재 page와 kw 값으로 questionRepository에서 조회한 값과 10개 단위로 페이징 처리한 값을 대입
        model.addAttribute("paging", paging);
        // model 객체에 "paging" 속성과 paging 객체를 값으로 전달(View에 전달하는 과정, 속성은 데이터 이름으로 이 이름을 이용하여 View에서 데이터 참조)
        model.addAttribute("kw", kw);
        // model 객체에 "kw" 속성과 kw 객체를 값으로 전달(View에 전달하는 과정, 속성은 데이터 이름으로 이 이름을 이용하여 View에서 데이터 참조)
        return "question_list";
        // model 객체로 전달된 내용을 기반으로 해당 페이지와 검색된 내용을 list로 출력하는 question_list.html(View) return
    }

    @GetMapping(value = "/detail/{id}")
    /* "question/detail/(question.id)"와 URL 연결
       value = "" 부분은 사실 상 차이가 없다고 봐도 됨 */
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        /* 질문의 상세 페이지를 보여주는 메서드로 Model, 질문의 id(URL에서 얻은 값), AnswerForm을 매개변수로 받음
           @PathVariable : 변하는 값을 얻을 때 사용하는데, 매개변수와 Mapping URL 쪽에서 사용한 이름이 동일해야 함. URL 경로에서 값을 추출하여 메서드의 인자로 전달할 때 사용 */
        /* 실행 흐름 : 해당 메서드에서는 question_list.html에서 <a th:href="@{|/question/detail/${question.id}|}" th:text="${question.subject}"></a> 를 통해 설정 도메인 + question.id 값이 URL로 조합
                       (question_list.html에는 QuestionController의 list 메서드에서 qustion 객체가  model을 통해 전달) -> 조합한 URL의 값을 QuestionController의 detail 메서드 - QuestionService로 받아서 사용
                       -> question 객체 값을 Model에 저장해서 question_detail.html로 전달 */
        Question question = this.questionService.getQuestion(id);
        // Question 클래스를 참조하는 question 변수에 questionService에서 현재 question의 id 값으로 questionRepository에서 조회한 값을 대입
        model.addAttribute("question", question);
        // model 객체에 "question" 속성과 question 객체를 값으로 전달(View에 전달하는 과정, 속성은 데이터 이름으로 이 이름을 이용하여 View에서 데이터 참조)
        return "question_detail";
        // model 객체로 전달된 내용을 기반으로 질문의 상세 페이지를 출력하는 question_detail.html(View) return
    }

    @PreAuthorize(("isAuthenticated()"))
    // 로그인한 상태인지 먼저 확인
    @GetMapping("/create")
    // "question/create"와 URL 연결
    public String questionCreate(QuestionForm questionForm) {
        /* 질문 작성하는 페이지를 보여주는 메서드로 QuestionForm을 매개변수로 받음(다만 작성된 내용이 없으므로 Null 상태일 것)
           QuestionForm을 받는 이유는 question_form.html 템플릿은 "질문 등록하기" 버튼을 통해 GET 방식으로 요청되더라도 th:object에 의해 QuestionForm 객체가 필요하기 때문 */
        return "question_form";
        // 질문을 작성하는 question_form.html(View)을 return
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @PostMapping("/create")
    // "question/create"와 URL 연결(POST 요청) -> POST 요청은 서버의 리소스를 새로 생성하거나 업데이트할 때 사용(DB로 따지면 Create에 가깝고, 멱등X(리소스를 생성/업데이트에 사용해서 POST 요청 발생 시 서버가 변경될 수 있음))
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        // 질문을 작성하는 메서드로 QuestionForm(@Valid 애너테이션으로 Form  클래스에 애너테이션으로 설정한 검증 기능 작동), QuestionForm의 검증된 결과를 받는 BindingResult, 현재 사용자 정보(principal)를 매개 변수로 받음
        if (bindingResult.hasErrors()) {
        // 만약 bindingResult에 에러가 있으면
            return "question_form";
            // 다시 질문을 작성하는 question_form.html(View)를 return
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        // SiteUser 클래스를 참조하는 siteUser 변수에 userService에서 현재 사용자 정보(principal)의 이름을 얻어와서 UserRepository에 findByusername으로 조회된 값을 대입
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        // questionService의 create메서드에서 Question 객체를 생성하고 questionForm으로 전달된 내용 중 Subject와 Content, 위에서 받아온 siteUser를 매개변수로 전달해 해당 내용을 객체에 저장한 뒤, QuestionRepository에 save로 저장
        return "redirect:/question/list";
        // 질문 목록을 보여주는 페이지인 "question/list"로 연결
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @GetMapping("/modify/{id}")
    // "question/modify/(question.id)"와 URL 연결(GET 요청)
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        // question을 수정하는 화면을 불러오는 메서드로, QuestionForm(현재 비어 있는 상태(Null)로, 기존 답변을 저장 -> View에 전달할 예정이기 때문에 선언), URL에서 받은 quetion의 id, 현재 사용자 정보(principal)를 매개변수로 받음
        Question question = this.questionService.getQuestion(id);
        // Question 클래스를 참조하는 question 변수에 questionService에서 현재 question의 id로 QuestionRepository에서 findById로 조회된 값을 대입
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
        // 만약 question의 getAuthor로 받아온 SiteUser를 참조한 author의 이름(getUsername)과 현재 사용자 정보(principal)의 이름이 같지 않다면
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
            /* "수정 권한이 없습니다"라는 메세지와 함께 오류 반환
               ResponseStatusException(HttpStatus.BAD_REQUEST) : 400 Bad Request HTTP 응답코드로 반환 */
        }
        questionForm.setSubject(question.getSubject());
        // 비어 있는 questionForm에 기존(현재) question의 subject를 setSubject로 저장
        questionForm.setContent(question.getContent());
        // 비어 있는 questionForm에 기존(현재) question의 content를 setContentfh 저장
        return "question_form";
        // 질문을 수정(작성)하는 question_form.html(View)에 기존 질문 제목과 내용을 함께 return
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @PostMapping("/modify/{id}")
    // "question/modify/(question.id)"와 URL 연결(POST 요청)
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id) {
        /* question을 수정하는 메서드로, QuestionForm(@Valid 애너테이션으로 Form  클래스에 애너테이션으로 설정한 검증 기능 작동), QuestionForm의 검증된 결과를 받는 BindingResult, 현재 사용자 정보(principal), question의 id를 매개 변수로 받음
           수정 과정의 전반적인 흐름 : 질문을 수정하고 저장하기 버튼을 누르면 질문이 수정되는 것이 아니라, 새로운 질문으로 등록되는데 이 문제는 템플릿(View) 폼 태그의 action을 잘 활용해 유연하게 대처
                                       question_form.html의 th:action을 삭제한 후  <form th:object="${questionForm}" method="post"> <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" /> 로 수정
                                       -> 폼 태그의 action 속성 없이 폼을 submit 하면, 폼의 action은 현재 URL 기준으로 전송. 즉, 질문 등록할 때는 /question/create이기 때문에 action 속성에 create가 설정되고, question/modify/{id}일 때는 action 속성에 modify로 설정
                                       th:action을 삭제하면 CSRF 값이 자동으로 생성되지 않기 때문에 hidden 형태의 input 엘리먼트를 수동으로 추가(input 태그는 사용자가 입력한 데이터를 폼과 함께 서버로 보내기 위한 데이터를 생성) */
        if(bindingResult.hasErrors()) {
        // 만약 bindingResult에 에러가 있으면
            return "question_form";
            // 다시 질문을 수정(작성)하는 question_form.html(View)를 return
        }
        Question question = this.questionService.getQuestion(id);
        // Question 클래스를 참조하는 question 변수에 questionService에서 현재 question의 id로 QuestionRepository에서 findById로 조회된 값을 대입
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
        // 만약 question의 getAuthor로 받아온 SiteUser를 참조한 author의 이름(getUsername)과 현재 사용자 정보(principal)의 이름이 같지 않다면
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
            // "수정 권한이 없습니다"라는 메세지와 함께 오류 반환
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        /* questionService의 modify메서드에 question과 새로 들어온 questionForm의 제목과 내용을 매개변수로 넣고 실행
           questionService.modify : qutestion 객체의 subject(제목)과 content(내용)에 매개변수로 들어온 제목과 내용을 넣고, 현재 날짜를 modifyDate에 넣은 뒤, 해당 객체를 questionRepository에 save로 저장 */
        return String.format("redirect:/question/detail/%s", id);
        // redirect로 해당 URL로 이동 -> question/detail/(question.id)로 id로 조회된 해당(수정한 현재) 질문의 상세 페이지
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @GetMapping("/delete/{id}")
    // "question/delete/(question.id)"와 URL 연결
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        // question을 삭제하는 메서드로 현재 사용자 정보(principal)와 URL에서 전달 받은 현재 질문의 id를 매개변수로 받음
        Question question = this.questionService.getQuestion(id);
        // Question 클래스를 참조하는 question 변수에 questionService에서 현재 question의 id로 QuestionRepository에서 findById로 조회된 값을 대입
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
        // 만약 question의 getAuthor로 받아온 SiteUser를 참조한 author의 이름(getUsername)과 현재 사용자 정보(principal)의 이름이 같지 않다면
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
            // "삭제 권한이 없습니다"라는 메세지와 함께 오류 반환
        }
        this.questionService.delete(question);
        // questionService의 delete 메서드를 통해 questionRepository에서 해당 question을 삭제
        return "redirect:/";
        //  redirect로 메인 페이지(/)로 연결되는데, 메인 페이지는 list 페이지로 redirect 시키기 때문에 "question/list"로 이동
    }

    @PreAuthorize("isAuthenticated()")
    // 로그인한 상태인지 먼저 확인
    @GetMapping("/vote/{id}")
    // "question/vote/(question.id)"와 URL 연결
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        // question을 추천하는 메서드로 현재 사용자 정보(principal)와 question의 id가 매개변수로(@PathVariable을 통해) 들어옴
        Question question = this.questionService.getQuestion(id);
        // Question 클래스를 참조하는 question 변수에 questionService에서 현재 question의 id로 QuestionRepository에서 findById로 조회된 값을 대입
        SiteUser siteUser = this.userService.getUser(principal.getName());
        // SiteUser 클래스의 siteUser 변수에 userService에서 현재 사용자 정보(principal)의 이름으로 userRepository에서 조회한 값을 대입
        this.questionService.vote(question, siteUser);
        /* questionService의 vote 메서드에 question과 siteUser를 매개변수로 넣음 -> vote 메서드에서 question의 voter Set(중복X)을 불러와서 현재 siteUser를 add하고 questionRepository에 정보가 바뀐 question을 save
           추천 숫자가 올라가는 곳은 question_detail.html에 th:text="${#lists.size(question.voter)}"로 구현되어 있음 */
        return String.format("redirect:/question/detail/%s", id);
        // redirect로 해당 URL로 이동 -> question/detail/(question.id)로 id로 조회된 해당(현재) 질문의 상세 페이지
    }
}
