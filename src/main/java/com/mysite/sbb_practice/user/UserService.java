package com.mysite.sbb_practice.user;

import com.mysite.sbb_practice.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String email, String password) {
    // SiteUser의 정보를 새로 저장하는 메서드로 username, email, password를 매개변수로 받음
        SiteUser user = new SiteUser();
        // StieUser 클래스를 참조한 user 변수에 SiteUser 객체를 생성해서 대입
        user.setUsername(username);
        // user의 Username을 username으로 저장
        user.setEmail(email);
        // user의 Email을 email로 저장
        user.setPassword(passwordEncoder.encode(password));
        /* user의 Password를 매개변수로 전달된 password를 passwordEncoder에 encode한 값을 저장
           passwordEncoder : 사용자의 비밀번호를 안전하게 저장하기 위해 사용되는 인코더 객체로, 보통 비밀번호는 평문이 아니라 알고리즘을 사용해 암호화된 형태로 저장
                             Spring Security에서는 PasswordEncoder 인터페이스를 제공하여 비밀번호 인코딩과 관련된 작업을 처리
                             이 인터페이스를 구현한 여러 클래스들이 있으며 BCryptPasswordEncoder, Argon2PasswordEncoder, Pbkdf2PasswordEncoder, SCryptPasswordEncoder 등이 있음
                             이 프로젝트에서는 SecurityConfig에 BCryptPasswordEncoder를 이용해 passwordEncoder를 구현하고 @Bean을 통해 Bean으로 등록 */
        this.userRepository.save(user);
        // 위에 저장한 내용을 userRepository에 save로 저장
        return user;
    }

    public SiteUser getUser(String username) {
    // username으로 검색한 SiteUser의 객체를 return 해주는 메서드
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        // SiteUser 클래스를 참조한 siteUser 변수에 userRepository에서 username으로 찾은 siteUser를 대입하는데, Optional을 통해 해당 siteUser가 없는 경우를 대비
        if(siteUser.isPresent()) {
        // 만약 siteUser의 값이 있다면(true)
            return siteUser.get();
            // siteUser 객체를 return(그냥 siteUser는 Optional<SiteUser>라서 해당 방식으로 SiteUser 객체 반환)
        } else {
        // 그 외에
            throw new DataNotFoundException("siteuser not found");
            // "siteuser not found" 메세지와 함께 DataNotFoundException 오류를 발생시킴
        }
    }
}
