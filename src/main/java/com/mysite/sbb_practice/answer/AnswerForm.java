package com.mysite.sbb_practice.answer;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {
    // Form : 화면에서 전달받은 입력 값을 검증하기 위해 사용
    @NotEmpty(message = "내용은 필수항목입니다.")
    /* @NotEmpty : Null 또는 빈 문자열("")을 허용하지 않음
       message : 검증이 실패할 경우 화면에 표시할 오류 메세지 */
    private String content;
    // String으로 내용을 받음
}
