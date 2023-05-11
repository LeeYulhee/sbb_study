package com.mysite.sbb_practice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
// DataNotFoundException이 발생하면 @ResponseStatus 애너테이션에 의해 404오류(HttpStatus.NOT_FOUND)가 나타날 것
public class DataNotFoundException extends RuntimeException {
// RuntimeException을 상속하여 DataNotFoundException 만듦
    private static final long serialVersionUID = 1L;
    /* serialVersionUID : 자바 직렬화 프로토콜에서 사용되는 고유 식별자
                                  Serializable 인터페이스를 구현한 클래스에서 사용됩니다. 이 인터페이스는 객체를 직렬화할 수 있도록 해주는데, 이때 serialVersionUID를 명시하면 클래스의 버전 정보를 명시적으로 지정할 수 있음
                                  직렬화된 객체를 역직렬화(deserialization)할 때, 클래스의 버전 정보를 확인하고 호환성 여부를 판단
                                  명시적으로 값을 지정하지 않으면 컴파일러가 자동으로 계산하여 부여
               직렬화 : 자바 객체를 이진 데이터(binary data)로 변환하는 과정을 말함. 이진 데이터로 변환된 객체는 파일이나 네트워크 전송 등을 통해 저장되거나 전송할 수 있음 */
    public DataNotFoundException(String message) {
    // 지정된 메세지로 DataNotFoundException 인스턴스 생성 -> message는 위 애너테이션으로 설정한 reason 속성이 클래스의 message 필드에 설정

        super(message);
        // RuntimeException 클래스의 생성자를 호출하여 지정된 메세지를 전달
    }
}
