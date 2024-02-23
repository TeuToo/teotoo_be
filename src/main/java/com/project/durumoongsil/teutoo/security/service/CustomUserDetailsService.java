package com.project.durumoongsil.teutoo.security.service;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findMemberByEmail(email)
                .map(member -> createUser(email, member))
                .orElseThrow(() -> new UsernameNotFoundException(email + "--> 데이터베이스에서 찾을 수 없습니다."));
    }

    private org.springframework.security.core.userdetails.User createUser(String email, Member member) {
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority(member.getRole().toString()));

        return new org.springframework.security.core.userdetails.User(member.getEmail(),
                member.getPassword(),
                simpleGrantedAuthorities);
    }
}
