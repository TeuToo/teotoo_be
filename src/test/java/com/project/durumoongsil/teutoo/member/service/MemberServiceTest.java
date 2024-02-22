package com.project.durumoongsil.teutoo.member.service;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.dto.MemberJoinDto;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp() {
        //given
        MemberJoinDto memberJoinDto = MemberJoinDto.builder()
                .name("변주환")
                .email("test@naver.com")
                .password("1111")
                .address("경기도 성남시 분당구")
                .sortRole(true)
                .build();

        Member member = Member.toEntity(memberJoinDto);

        ArgumentCaptor<Member> memberArgumentCaptor = ArgumentCaptor.forClass(Member.class);

        // when
        memberService.signUp(memberJoinDto);


        // then
        verify(memberRepository).save(memberArgumentCaptor.capture());
        Member captorValue = memberArgumentCaptor.getValue();
        assertThat(captorValue.getRole()).isEqualTo(Role.TRAINER);
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}