package com.mysite.sbb_practice;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@Component
/* @Component : 스프링 컨테이너에 등록하여 해당 클래스의 인스턴스를 스프링에서 직접 관리할 수 있게 됨 -> 개발자가 직접 작성한 클래스를 Bean으로 등록하는 애너테이션
                타임리프에서 @로 접근 가능
                다른 클래스에서 CommonUtil 클래스의 인스턴스를 생성하고 사용할 때에도 스프링의 의존성 주입 기능을 활용하여 쉽게 관리할 수 있음 */
public class CommonUtil {
// 마크다운을 적용하기 위해서 생성한 클래스
    public String markdown(String markdown) {
    /* markdown 메서드는 마크다운 텍스트를 HTML 문서로 변환하여 return : 메서드 내부의 구성은 Markdown에서 사용 가이드로 제공한 부분
       해당 마크다운은 템플릿에 적용 -> question_detail.html의 질문 입력 <div class="card-text" th:utext="${@commonUtil.markdown(question.content)}"></div> 과
       답변 입력 <div class="card-text" th:utext="${@commonUtil.markdown(answer.content)}"></div> 에 적용
       th:utext : th:text를 사용하면 HTML의 태그들이 이스케이프(escape) 처리 되어 태그들이 그대로 화면에 보임
                  변수에서 받은 값에 HTML 태그가 있다면 태그 값을 반영해서 표시
       th:text : 태그 안의 텍스트를 서버에서 전달 받은 값에 따라 표현하고자 할 때 사용 */
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }
}
