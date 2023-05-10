package com.mysite.sbb_practice.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {
// Spring Security에 등록하여 사용할 UserSecurityService는 Spring Security가 제공하는 UserDetailsService 인터페이스를 구현해야 함(UserDetailsService는 loadUserByUsername 메서드를 구현하도록 강제하는 인터페이스)

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    /* 해당 메서드는 사용자명으로 비밀번호를 조회하여 return하는 메서드 -> 사용자 이름을 입력 받아 사용자의 정보와 권한을 조회하여 Security에서 사용할 수 있는 형태인 UserDetails로 변환해주는 역할
       Security는 loadUserByUsername 메서드에 의해 리턴된 User 객체의 비밀번호가 화면으로부터 입력받은 비밀번호와 일치하는지를 검사하는 로직을 내부적으로 갖고 있음
       UserDetails : ID, PW, 권한 정보를 담은 인터페이스 -> User로 return */
        Optional<SiteUser> _siteUser = this.userRepository.findByusername(username);
        // SiteUser 클래스를 참조한 _siteUser 변수에 userRepository에서 username으로 찾은 siteUser를 대입하는데, Optional을 통해 해당 siteUser가 없는 경우를 대비
        if(_siteUser.isEmpty()) {
        // 만약 _siteUser가 비어 있다면
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
            // "사용자를 찾을 수 없습니다." 메세지와 함께 UsernameNotFoundException 오류를 발생시킴
        }
        SiteUser siteUser = _siteUser.get();
        // SiteUser 클래스를 참조한 siteUser 변수에 _siteUser.get으로 객체 할당
        List<GrantedAuthority> authorities = new ArrayList<>();
        /* GrantedAuthority 클래스를 참조한 authorities 변수를 List로 생성(권한이 여러 개일 수도 있기 때문)
           GrantedAuthority : 사용자 권한을 나타내는 클래스 */
        if("admin".equals(username)) {
        // 만약 username(사용자ID)가 admin과 같다면
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
            /* authorities(권한 목록)에 관리자 권한을 추가 -> SimpleGrantedAuthority에 UserRole에서 정의한 ADMIN의 값을 넣어 객체를 생성한 뒤에 authorities에 추가
               SimpleGrantedAuthority : GrantedAuthority 인터페이스를 구현한 클래스로, 해당하는 권한을 추가하여 객체를 생성 */
        } else {
        // 그 외에
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
            // authorities(권한 목록)에 UserRole에서 정의한 USER의 값을 넣어 생성한 SimpleGrantedAuthority 객체를 생성해 추가
        }
        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);
        // siteUser의 username과 password, authorities(권한 목록)을 넣은 User 객체를 생성해서 return
    }
}
