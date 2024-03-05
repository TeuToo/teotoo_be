package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.domain.FilePath;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramManageResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.PtProgramUpdateDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtImgRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.repository.PtProgramRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.util.PtProgramConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PtProgramService {

    private final SecurityService securityService;
    private final MemberRepository memberRepository;
    private final TrainerInfoRepository trainerInfoRepository;
    private final PtProgramRepository ptProgramRepository;
    private final PtImgRepository ptImgRepository;
    private final FileService fileService;
    private final PtProgramConverter ptProgramConverter = new PtProgramConverter();


    // PT Program 등록
    @Transactional
    public void register(PtProgramRegDto ptProgramRegDto) {

        String userEmail = securityService.getLoginedUserEmail();

        // trainer info id 만 조회,
        Long trainerInfoId = trainerInfoRepository.findTrainerInfoIdByMemberEmail(userEmail)
                .orElseThrow(() -> new NotFoundUserException("트레이너 소개 등록 정보를 찾을 수 없습니다."));

        TrainerInfo trainerInfo = TrainerInfo.builder()
                .id(trainerInfoId)
                .build();

        PtProgram ptProgram = ptProgramConverter.toPtProgram(ptProgramRegDto, trainerInfo);

        ptProgramRepository.save(ptProgram);

        if (ptProgramRegDto.getAddPtImgList() != null) {
            // pt 프로그램 이미지 저장
            savePtProgramImg(ptProgram, ptProgramRegDto.getAddPtImgList());
        }
    }

    // PT Program 업데이트
    @Transactional
    public void update(Long trainerId, PtProgramUpdateDto ptProgramUpdateDto) {
        String userEmail = securityService.getLoginedUserEmail();


        // 프로그램과 이미지,파일 엔티티 한번에 조회
        PtProgram ptProgram = ptProgramRepository.findByIdAndMemberEmailWithPtImgAndFile(trainerId, userEmail)
                .orElseThrow(() -> new NotFoundUserException("트레이너 프로그램 등록 정보를 찾을 수 없습니다."));

        // PtProgram 수정
        ptProgram.updatePrice(ptProgramUpdateDto.getPrice());
        ptProgram.updatePtCnt(ptProgramUpdateDto.getPtCnt());
        ptProgram.updateTitle(ptProgramUpdateDto.getTitle());
        ptProgram.updateContent(ptProgramUpdateDto.getContent());

        // 사용자가 삭제한 이미지가 존재한다면,
        if (ptProgramUpdateDto.getDelPtImgList() != null) {
            List<String> reqDelImgList = ptProgramUpdateDto.getDelPtImgList();

            List<PtImg> delPtImgList = ptProgram.getPtImgList().stream()
                    .filter(ptImg -> reqDelImgList.contains(ptImg.getFile().getFileName())).toList();

            deletePtProgramImg(delPtImgList);
        }

        // 자격사항 이미지 저장
        if (ptProgramUpdateDto.getAddPtImgList() != null) {
            savePtProgramImg(ptProgram, ptProgramUpdateDto.getAddPtImgList());
        }
    }

    private void savePtProgramImg(PtProgram ptProgram, List<MultipartFile> addPtImgList) {
        for (MultipartFile file : addPtImgList) {
            File savedFile = fileService.saveImgToDB(FilePath.PT_PROGRAM.getPath(), file);
            PtImg ptImg = new PtImg(ptProgram, savedFile);
            ptImgRepository.save(ptImg);
        }
    }

    private void deletePtProgramImg(List<PtImg> delPtImgList) {
        List<Long> delImgIdList = delPtImgList.stream().map(PtImg::getId).toList();
        ptImgRepository.deleteAllById(delImgIdList);

        List<String> savedDelImgList = delPtImgList.stream().map(ptImg -> ptImg.getFile().getFileName()).toList();
        fileService.deleteImgListToDB(FilePath.PT_PROGRAM.getPath(), savedDelImgList);
    }


    // PT 프로그램 관리페이지 데이터 조회
    public PtProgramManageResDto getPtProgramListForManagement() {
        String memberEmail = securityService.getLoginedUserEmail();

        Member member = memberRepository.findMemberByEmail(memberEmail)
                .orElseThrow(() -> new NotFoundUserException("사용자를 찾을 수 없습니다."));

        List<PtProgramResDto> ptProgramResDtoList = this.getPtProgramList(memberEmail);


        // 사용자 프로필 이미지
        ImgResDto imgResDto = ImgResDto.create(member.getProfileOriginalImageName(),
                    fileService.getImgUrl(FilePath.MEMBER_PROFILE.getPath(), member.getProfileImageName()));

        return ptProgramConverter.toPtProgramManageResDto(ptProgramResDtoList, member, imgResDto);
    }


    // PT 프로그램 리스트 조회
    public List<PtProgramResDto> getPtProgramList(String memberEmail) {
        List<PtProgram> ptProgramList = ptProgramRepository.findByMemberEmailWithPtImg(memberEmail);

        return ptProgramList.stream().map(ptProgram -> {

            // 각 프로그램에 대한 이미지 리스트
            List<ImgResDto> imgResDtoList = ptProgram.getPtImgList()
                    .stream().map(ptImg -> {
                return ImgResDto.create(ptImg.getFile().getFileName(),
                        fileService.getImgUrl(ptImg.getFile().getFilePath(), ptImg.getFile().getFileName())
                );
            }).toList();

            return ptProgramConverter.toPtProgramResDto(ptProgram, imgResDtoList);
        }).toList();
    }

    @Transactional
    public void deletePtProgram(Long programId) {
        String memberEmail = securityService.getLoginedUserEmail();

        /*
            사용자 email에 대응되는 PT 프로그램을 조회한다, 만약 없다면, 해당 사용자의 프로그램을 찾을 수 없습니다.
            1. 이미지 삭제 ( file 및 ptImg 삭제..)
            2. 프로그램 삭제
         */
        PtProgram ptProgram = ptProgramRepository.findByIdAndMemberEmailWithPtImgAndFile(programId, memberEmail)
                .orElseThrow(() -> new NotFoundUserException("트레이너 프로그램 등록 정보를 찾을 수 없습니다."));

        // pt program 이미지 삭제 및 엔티티 삭제
        deletePtProgramImg(ptProgram.getPtImgList());

        ptProgramRepository.delete(ptProgram);
    }


}
