package com.project.durumoongsil.teutoo.member.service;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.durumoongsil.teutoo.member.domain.Role.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;


    public void signUp(MemberJoinDto memberJoinDto) {
        Role role = getRole(memberJoinDto);

        Member member = Member.toEntity(memberJoinDto);

        member.setRole(role);

        memberRepository.save(member);
    }

    private Role getRole(MemberJoinDto memberJoinDto) {
        return memberJoinDto.getSortRole() ? TRAINER : USER;
    }
}
