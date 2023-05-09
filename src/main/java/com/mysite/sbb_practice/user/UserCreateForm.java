package com.mysite.sbb_practice.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {
    // Form : 화면에서 전달받은 입력 값을 검증하기 위해 사용
    @Size(min = 3, max = 24)
    // 글자의 길이가 3바이트보다 작을 수 없고, 24바이트보다 클 수 없음
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    // Null 또는 빈 문자열 금지, 빈 문자열일 경우 "사용자ID는 필수항목입니다." 오류 메세지 표시
    private String username;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    // Null 또는 빈 문자열 금지, 빈 문자열일 경우 "비밀번호는 필수항목입니다." 오류 메세지 표시
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    // Null 또는 빈 문자열 금지, 빈 문자열일 경우 "비밀번호 확인은 필수항목입니다." 오류 메세지 표시
    private String password2;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    // Null 또는 빈 문자열 금지, 빈 문자열일 경우 "이메일은 필수항목입니다." 오류 메세지 표시
    @Email
    // 주어진 문자열이 유효한 이메일 주소 형식인지 검사하는 역할
    private String email;
}
