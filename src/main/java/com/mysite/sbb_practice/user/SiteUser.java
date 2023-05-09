package com.mysite.sbb_practice.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {

    @Id
    // 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /* strategy : 고유 번호 생성하는 옵션
       GenerationType.IDENTITY : 해당 컬럼에 독립적인 시퀀스를 생성하여 번호를 증가시킬 때 사용
                                 == Auto_increment */
    private Long id;

    @Column(unique = true)
    /* @Column : 컬럼 명과 일치, 컬럼의 세부 설정을 위해 사용
       unique = true : 중복된 값은 저장되지 않음 */
    private String username;

    private String password;

    @Column(unique = true)
    /* @Column : 컬럼 명과 일치, 컬럼의 세부 설정을 위해 사용
       unique = true : 중복된 값은 저장되지 않음 */
    private String email;
}
