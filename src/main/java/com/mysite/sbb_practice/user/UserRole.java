package com.mysite.sbb_practice.user;

import lombok.Getter;

@Getter
public enum UserRole {
/* Security는 인증 뿐 아니라 권한도 권리하는데, 인증 후에 사용자에게 부여할 권한이 필요해서 권한을 정의하는 클래스 생성
   enum : 열거 자료형으로 연관된 상수들의 집합을 정의하기 위한 데이터 타입
          입력될 수 있는 값이 한정적인니 유효성 검사를 보장
          문자열을 열거형 상수와 연결하는 것은 USER_ROLE 상수의 의미를 명확하게 함 */
    ADMIN("ROLE_ADMIN"),
    // ADMIN 상수 생성하고, 상수의 값을 "ROLE_ADMIN"으로 설정
    USER("ROLE_USER");
    // USER 상수 생성하고, 상수의 값을 "ROLE_USER"로 설정

    UserRole(String value) {
    // 상수의 값을 받는 생성자 생성
        this.value = value;
        // 매개변수로 들어오는 value를 해당 클래스의 value 변수에 대입

    }

    private String value;
    // 해당 클래스의 변수인 value 선언
}
