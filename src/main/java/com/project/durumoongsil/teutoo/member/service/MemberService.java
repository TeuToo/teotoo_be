package com.project.durumoongsil.teutoo.member.service;

import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.dto.MemberUpdateDto;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.durumoongsil.teutoo.member.domain.Role.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public void signUp(MemberJoinDto memberJoinDto) {
        Role role = getRole(memberJoinDto);
        Member member = Member.toEntity(memberJoinDto);

        String encodedPassword = passwordEncoder.encode(member.getPassword());
        member.setRole(role);
        member.setPassword(encodedPassword);

        memberRepository.save(member);
    }

    public void updateInfo(Long id, MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다."));

        //member.updateInfo(memberUpdateDto);
    }

    private Role getRole(MemberJoinDto memberJoinDto) {
        return memberJoinDto.getSortRole() ? TRAINER : USER;
    }
}
