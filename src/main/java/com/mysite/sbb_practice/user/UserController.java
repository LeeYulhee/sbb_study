package com.mysite.sbb_practice.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    // "user/signup"과 URL 연결
    /* @GET 요청 : GET은 서버의 리소스에서 데이터를 요청할 때 사용(DB로 따지면 GET은 SELECT에 가깝고, 멱등(연산을 여러 번 적용해도 결과가 달라지지 않음 = 리소스를 조회하는 것이라))
                   GET은 요청을 전송할 때, 힐요한 데이터를 Body에 담지 않고, 쿼리스트링(URL 끝에 ?와 함께 이름과 앖으로 쌍울 이루는 요청 파라미터)을 통해 전송
                   GET 요청은 캐시가 가능(GET을 통해 서버에 리소스를 요청할 때, 웹 캐시가 요청을 가로채 리소스를 다시 다운로드 하는 대신 리소스의 복사본 반환)함
                   GET 요청은 브라우저 히스토리에 남으며, 길이에 제한이 있고, 보안 상의 문제로 중요한 정보를 다루지 않는 것이 좋음*/
    public String signup(UserCreateForm userCreateForm) {
    // 회원가입 하는 form을 연결해주는 메서드로 UserCreateForm을 매개변수로 받음
        return "signup_form";
        // 회원가입 창인 signup_form.html을 return(화면에 띄움)
    }

    @PostMapping("/signup")
    // "user/signup"과 URL 연결
    /* @PostMapping : 매핑을 담당하는 역할로, POST 요청만 받아들일 경우에 사용
                      POST 요청은 서버의 리소스를 새로 생성하거나 업데이트할 때 사용(DB로 따지면 Create에 가깝고, 멱등X(리소스를 생성/업데이트에 사용해서 POST 요청 발생 시 서버가 변경될 수 있음))
                      POST는 리소스를 생성/변경하기 위해 설계되었기 때문에, HTTP 메세지의 Body에 담아 전송
                      POST 요청은 캐시되지 않고, 브라우저 히스토리에 남지 않으며, 데이터 길이에 제한이 없음
                      메서드의 매개변수 이름과 템플릿의 항목 이름이 같아야 함
                      @GetMapping과 매개변수가 다른 경우 같은 메서드 이름을 사용할 수 있음(메서드 오버로딩)
                      보안 등을 생각한다면 조회 빼고는 POST 처리하는 게 좋을 수도 있음
                      GET과 POST를 제외한 요청은 HTML에서 적용이 안 됨 -> hiddenmethod를 키면 실제는 POST지만 요청을 이식해서 자동 변환(PATCH : 수정, DELETE : 삭제) */
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
    // 회원가입 창에 작성한 정보를 저장(회원 등록)하는 메서드로 @Valid를 통해 UserCreateForm에 맞춰 검증된 결과인 BindingResult를 매개변수로 받음
        if(bindingResult.hasErrors()) {
        // 만약 Form을 검사한 결과에 에러가 있다면
            return "signup_form";
            // 다시 signup_form.html(회원가입 창) 템플릿을 return == 다시 회원가입 창으로 돌아감
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
        // 만약 userCreateForm에서 받아온 Password1이 userCreateForm에서 받아온 Password2와 같지 않다면
            bindingResult.rejectValue("password2", "passwordIncorrect", "2개의 패스워드가 일치하지 않습니다.");
            /* bindingResult의 rejectValue 메서드를 이용해 오류를 발생시킴 -> password2의 필드에 passwordIncorrect라는 오류 코드와 "2개의 패스워드가 일치하지 않습니다."라는 오류 발생
               rejectValue : 필드에 대한 오류 코드를 등록할 때 사용. 유효성 검사에서 오류를 만들 수 있음. bindingResult.rejectValue(필드명, 오류 코드, 오류 메세지) */
            return "signup_form";
            // 다시 signup_form.html(회원가입 창) 템플릿을 return == 다시 회원가입 창으로 돌아감
        }

        try {
        // try~catch문으로 오류 대비
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
            /* userService의 create 메서드를 통해 매개변수로 전달하는 userCreateForm의 username, email, password1을 회원의 정보로 저장
               create 메서드에서 SiteUser 객체를 만들고, username, email, passwerd를 set으로 저장하고 해당 내용을 userRepository에 save로 저장한 뒤 user를 return */
        } catch (DataIntegrityViolationException e) {
        // Entity의 unique에 의해 ID 또는 email이 중복일 때 오류 발생하면
            e.printStackTrace();
            // printStackTrace : 어떤 부분에 예외가 발생했는지 추적하기 위한 메서드로 콘솔에 내용이 출력됨
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            /* bindingResult.reject를 통해 signupFailed라는 오류 코드와 "이미 등록된 사용자입니다."라는 오류 메세지 발생
               reject : 특정 필드의 오류가 아닌 일반적인 오류를 등록할 때 사용(전 객체에 대한 글로벌 에러코드를 추가하고, 에러코드에 대한 메세지가 존재하지 않을 경우 defaultMessage를 사용).
                         bindingResult.reject(오류 코드, 오류 메세지) */
            return "signup_form";
            // 다시 signup_form.html(회원가입 창) 템플릿을 return == 다시 회원가입 창으로 돌아감
        } catch (Exception e) {
        // 그 외의 오류가 발생하면
            e.printStackTrace();
            // printStackTrace : 어떤 부분에 예외가 발생했는지 추적하기 위한 메서드로 콘솔에 내용이 출력됨
            bindingResult.reject("signupFailed", e.getMessage());
            // bindingResult.reject를 통해 signupFailed라는 오류 코드와 오류의 메세지를 오류 메세지로 오류 발생
            return "signup_form";
            // 다시 signup_form.html(회원가입 창) 템플릿을 return == 다시 회원가입 창으로 돌아감
        }

        return "redirect:/";
        // redirect로 해당 "/" URL로 이동 -> MainController에 "/" URL은 "question/list"로 redirect -> "question/list"로 이동
    }

    @GetMapping("/login")
    // "user/login"과 URL 연결
    public String login() {
    // login 창을 띄워주는 메서드
        return "login_form";
        // login_form.html 템플릿을 return하며 로그인 화면을 띄움
    }
}
