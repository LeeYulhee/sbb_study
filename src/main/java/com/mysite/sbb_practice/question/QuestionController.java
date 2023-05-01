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
           value = "page", defaultValue = "0" : URL에 페이지 파라미터 page가 전달되지 않은 경우 디폴트 값이 0으로 설정
           value = "kw", defaultValue = "" : URL 페이지에 파라미터 kw가 전될되지 않은 경우 디폴트 값이 ""로 설정 -> 모든 값을 검색한다는 뜻 == 모든 목록 출력 */
        /* 실행 흐름 : Controller에 매핑된 URL로 요청이 들어오면, questionService의 getList 메서드 실행 -> questionService의 getList는 int page를 받아 PageRequest.of(page, 개수)를 Pageable 객체에 넣음
                       -> questionService는 questionRepository에서 pageable(page, 개수/페이징 처리)이 적용된 findAll을 return -> Page<Question> 객체에 questionService.getList한 값이 들어감
                       -> model.addAttribute로 paging 객체를 "paging" 속성으로 전달 -> Controller는 해당 값을 전달 받고 처리된 question_list.html(View) return
         */
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
    // "question/detail/(question.id)"와 URL 연결
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
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, @PathVariable("id") Integer id) {
        if(bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
