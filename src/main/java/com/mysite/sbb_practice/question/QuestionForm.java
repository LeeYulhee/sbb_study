package com.mysite.sbb_practice.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    // Form : 화면에서 전달받은 입력 값을 검증하기 위해 사용
    @NotEmpty(message = "제목은 필수항목입니다.")
    /* @NotEmpty : Null 또는 빈 문자열("")을 허용하지 않음
       message : 검증이 실패할 경우 화면에 표시할 오류 메세지 */
    @Size(max=200)
    // 글자 최대 길이는 200바이트를 넘을 수 없음
    private String subject;

    @NotEmpty(message = "내용은 필수항목입니다.")
    // Null 또는 빈 문자열 금지, 빈 문자열일 경우 "내용은 필수항목입니다." 오류 메세지 표시
    private String content;
}
