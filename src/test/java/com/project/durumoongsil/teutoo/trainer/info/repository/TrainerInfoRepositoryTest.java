package com.project.durumoongsil.teutoo.trainer.info.repository;

import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.domain.Role;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.dto.TrainerListReqDto;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrainerInfoRepositoryTest {

    @Autowired
    TrainerInfoRepository trainerInfoRepository;
    @Autowired
    MemberRepository memberRepository;


    @BeforeAll
    public void setData() {
        String[] names = {"가가가", "김김김", "이이이", "박박박", "한한한"};
        String[] addresses = {"서울특별시", "경기도", "충청도", "강원도", "전라도"};

        for (int i = 0; i < names.length; i++) {
            Member testMember = Member.builder()
                    .name(names[i])
                    .address(addresses[i])
                    .role(Role.TRAINER)
                    .build();

            memberRepository.save(testMember);

            TrainerInfo testTrainerInfo = TrainerInfo.builder()
                    .simpleIntro("안녕하세요...")
                    .gymName("GYM"+i)
                    .introContent("안녕하세요 안녕하세요 안녕하세요")
                    .member(testMember)
                    .build();

            trainerInfoRepository.save(testTrainerInfo);
        }
    }

    @Test
    @DisplayName("트레이너 목록 조회 테스트 - 트레이너 이름으로 검색")
    public void trainerListSearch1() {
        TrainerListReqDto trainerListReqDto = new TrainerListReqDto();

        trainerListReqDto.setSearchTrainer("aaa");
        trainerListReqDto.setSize(10);
        trainerListReqDto.setPage(0);
        trainerListReqDto.setSort("alpha");
        trainerListReqDto.setSearchTrainer("김김김");
        trainerListReqDto.setDirection("desc");

        List<TrainerInfo> trainerInfoList = trainerInfoRepository.findBySearchCondition(trainerListReqDto).toList();

        assertEquals(trainerInfoList.size(), 1);
        assertEquals(trainerInfoList.get(0).getMember().getName(), "김김김");
    }

    @Test
    @DisplayName("트레이너 목록 조회 테스트 - 헬스장 이름으로 검색")
    public void trainerListSearch2() {
        TrainerListReqDto trainerListReqDto = new TrainerListReqDto();

        trainerListReqDto.setSize(10);
        trainerListReqDto.setPage(0);
        trainerListReqDto.setSort("alpha");
        trainerListReqDto.setSearchGym("GYM3");
        trainerListReqDto.setDirection("desc");

        List<TrainerInfo> trainerInfoList = trainerInfoRepository.findBySearchCondition(trainerListReqDto).toList();

        assertEquals(trainerInfoList.size(), 1);
        assertEquals(trainerInfoList.get(0).getGymName(), "GYM3");
    }

    @Test
    @DisplayName("트레이너 목록 조회 테스트 - 지역으로 검색")
    public void trainerListSearch3() {
        TrainerListReqDto trainerListReqDto = new TrainerListReqDto();

        trainerListReqDto.setSize(10);
        trainerListReqDto.setPage(0);
        trainerListReqDto.setSort("alpha");
        trainerListReqDto.setDirection("desc");
        trainerListReqDto.setSearchLocation("경기도");

        List<TrainerInfo> trainerInfoList = trainerInfoRepository.findBySearchCondition(trainerListReqDto).getContent();

        assertEquals(trainerInfoList.size(), 1);
        assertEquals(trainerInfoList.get(0).getMember().getAddress(), "경기도");
    }

    @Test
    @DisplayName("트레이너 목록 조회 테스트 - 필터링x 이름은 오름차순")
    public void trainerListSearch4() {
        TrainerListReqDto trainerListReqDto = new TrainerListReqDto();

        trainerListReqDto.setSize(10);
        trainerListReqDto.setPage(0);
        trainerListReqDto.setSort("alpha");
        trainerListReqDto.setDirection("asc");

        List<TrainerInfo> trainerInfoList = trainerInfoRepository.findBySearchCondition(trainerListReqDto).getContent();

        assertEquals(trainerInfoList.size(), 5);
        assertEquals(trainerInfoList.get(0).getMember().getName(), "가가가");
        assertEquals(trainerInfoList.get(1).getMember().getName(), "김김김");
        assertEquals(trainerInfoList.get(2).getMember().getName(), "박박박");
        assertEquals(trainerInfoList.get(3).getMember().getName(), "이이이");
    }

    @Test
    @DisplayName("트레이너 목록 조회 테스트 - 페이지네이션 테스트, 필터링x 이름은 오름차순")
    public void trainerListSearch5() {
        TrainerListReqDto trainerListReqDto1 = new TrainerListReqDto();

        trainerListReqDto1.setSize(4);
        trainerListReqDto1.setPage(0);
        trainerListReqDto1.setSort("alpha");
        trainerListReqDto1.setDirection("asc");

        Page<TrainerInfo> trainerInfoPage = trainerInfoRepository.findBySearchCondition(trainerListReqDto1);

        assertEquals(trainerInfoPage.getTotalPages(), 2);
        assertEquals(trainerInfoPage.getTotalElements(), 5);
        assertEquals(trainerInfoPage.getNumberOfElements(), 4);
        assertEquals(trainerInfoPage.getNumber(), 0);

        TrainerListReqDto trainerListReqDto2 = new TrainerListReqDto();

        trainerListReqDto2.setSize(4);
        trainerListReqDto2.setPage(1);
        trainerListReqDto2.setSort("alpha");
        trainerListReqDto2.setDirection("asc");

        trainerInfoPage = trainerInfoRepository.findBySearchCondition(trainerListReqDto2);

        assertEquals(trainerInfoPage.getTotalPages(), 2);
        assertEquals(trainerInfoPage.getTotalElements(), 5);
        assertEquals(trainerInfoPage.getNumberOfElements(), 1);
        assertEquals(trainerInfoPage.getNumber(), 1);
    }

}