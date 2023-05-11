package com.mysite.sbb_practice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
// 스프링의 환결설정 파일임을 의미하는 애너테이션
@EnableWebSecurity
/* @EnableWebSecurity : Security 구성 클래스에서 다양한 보안 구성을 수행할 수 있으며, 인증 및 권한 부여 매커니즘을 사용하여 웹 애플리케이션 보안을 확보
                        모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션
	                    원래는 WebSecurityConfigurerAdapter를 상속받은 구성 클래스에 적용
	                    상속받지 않아도 애너테이션을 사용하여 Security 활성화하고, 이를 통해 Security를 구성하는데 필요한 설정 클래스를 자동으로 등록
	                    해당 에너테이션을 사용하면 내부적으로 SpringSecurityFilterChain이 동작하여 URL 필터가 적용 */
@EnableMethodSecurity(prePostEnabled = true)
// 메서드 수준의 보안을 적용하도록 지시하는 것으로 prePostEnabled = true는 Pre/Post Annotation 방식을 활성화 시키는 것을 의미
public class SecurityConfig {
    @Bean
    // @Bean 애노테이션을 이용하여 SecurityFilterChain 객체 생성
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    /* SecurityFilterChain 객체 : Security가 HTTP 요청을 처리할 때, 적용되는 필터 체인을 나타내는 객체
       FilterChain :  HTTP 요청이 처리될 때, 이 요청을 처리하는 필터들의 연속적인 체인을 의미
                      HTTP 요청이 들어오면 체인의 시작점에서부터 필터들이 차례대로 실행되고, 각 필터는 이전 필터의 결과를 전달받아 처리를 수행하고 다음 필터에게 결과 전달
                      체인의 끝까지 실행된 후, 서블릿이 요청을 처리하고, 응답을 반환
       HttpSecurity 객체 : WebSecurityConfigurerAdapter 상속한 구성 클래스에서 configure(HttpSecurity http) 메서드를 오버라이딩하여 생성
                           상속받지 않아도 애너테이션을 사용해 활성화할 수 있음 */

        http.authorizeHttpRequests().requestMatchers(
        // authorizeRequests() : HTTP 요청에 대한 보안 설정을 시작하는 메서드
                new AntPathRequestMatcher("/**")).permitAll()
                // 인증되지 않은 요청을 허락한다는 의미 -> 로그인을 하지 않아도 모든 페이지에 접근할 수 있음
            .and()
                // 스프링 시큐리티의 로그인 설정을 담당하는 부분
                .formLogin()
                // 로그인 페이지와 로그인 성공/실패 처리에 대한 설정을 추가하는 메서드
                .loginPage("/user/login")
                // 로그인 페이지의 URL "user/login"으로 설정
                .defaultSuccessUrl("/")
                // 성공 시에 이동하는 default 페이지 URL은 "/" -> redierct로 "question/list"(질문 목록 페이지)로 이동
            .and()
                .logout()
                // 로그아웃 처리에 대한 설정을 추가하는 메서드
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                // 로그아웃 URL을 "user/logout"으로 설정. AntPathRequestMatcher 클래스는 특정 URL과 일치하는지 확인하는 데 사용
                .logoutSuccessUrl("/")
                // 로그 아웃 성공 시에 이동하는 URL은 "/" -> redierct로 "question/list"(질문 목록 페이지)로 이동
                .invalidateHttpSession(true)
                // invalidateHttpSession는 사용자가 로그아웃할 때 사용자의 HTTP 세션을 무효화하는 데 사용 -> logout 메서드와 함께 사용해야 함
                ;
        return http.build();
        /* 스프링 시큐리티의 HTTP 필터 체인을 반환. 이 필터 체인은 요청이 웹 애플리케이션에 들어오면 실행(필터 체인은 요청이 보안 요구 사항을 충족하는지 확인하는 데 사용)
           build : 필터 체인을 구성하는데 사용
           http : 필터 체인을 구성하는 데 사용(http는 스프링 시큐리티의 HTTP 필터 체인을 나타냄) */
    }

    @Bean
    PasswordEncoder passwordEncoder() {
    // 사용자의 비밀번호는 보안을 위해 암호화 해야 하기 때문에 PasswordEncoder를 사용하기 위해 만든 메서드
        return new BCryptPasswordEncoder();
        // BCryptPasswordEncoder는 BCrypt 해싱 함수(BCrypt hashing function)를 사용해서 비밀번호를 암호화
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    /* AuthenticationManager는 Security의 인증을 담당
       @Bean 생성 시, 스프링의 내부 동작으로 인해 위에서 작성한 UserSecurityService와 PasswordEncoder가 자동으로 설정 */
        return authenticationConfiguration.getAuthenticationManager();
    }
}
