package com.codestates.auth;

import com.codestates.auth.HelloAuthorityUtils;
import com.codestates.member.Member;
import com.codestates.member.MemberService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

//@Component
public class HelloUserAuthenticationProvider implements AuthenticationProvider {
    private final MemberService memberService;
    private final HelloAuthorityUtils authorityUtils;
    private final PasswordEncoder passwordEncoder;

    public HelloUserAuthenticationProvider(MemberService memberService,
                                           HelloAuthorityUtils authorityUtils,
                                           PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.authorityUtils = authorityUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;

        String username = authToken.getName();
        Optional.ofNullable(username).orElseThrow(() -> new UsernameNotFoundException("Invalid User name or User Password"));
        Member member = memberService.findMember(username);

        String password = member.getPassword();
        verifyCredentials(authToken.getCredentials(), password);

        Collection<? extends GrantedAuthority> authorities = authorityUtils.createAuthorities(member.getRoles());

        return new UsernamePasswordAuthenticationToken(username, password, authorities);
    }

    // HelloUserAuthenticationProvider가 Username/Password 방식의 인증을 지원한다는 것을 Spring Security에게 알려준다.
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    private void verifyCredentials(Object credentials, String password) {
        if (!passwordEncoder.matches((String)credentials, password)) {
            throw new BadCredentialsException("Invalid User name or User Password");
        }
    }
}
