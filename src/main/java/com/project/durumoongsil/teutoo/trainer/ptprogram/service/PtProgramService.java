package com.project.durumoongsil.teutoo.trainer.ptprogram.service;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.common.domain.FilePath;
import com.project.durumoongsil.teutoo.common.dto.ImgResDto;
import com.project.durumoongsil.teutoo.common.service.FileService;
import com.project.durumoongsil.teutoo.exception.NotFoundUserException;
import com.project.durumoongsil.teutoo.exception.PtProgramNotFoundException;
import com.project.durumoongsil.teutoo.exception.TrainerInfoNotFoundException;
import com.project.durumoongsil.teutoo.member.domain.Member;
import com.project.durumoongsil.teutoo.member.repository.MemberRepository;
import com.project.durumoongsil.teutoo.security.service.SecurityService;
import com.project.durumoongsil.teutoo.trainer.info.domain.TrainerInfo;
import com.project.durumoongsil.teutoo.trainer.info.repository.TrainerInfoRepository;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtProgram;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtProgramManageResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtProgramRegDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.response.PtProgramResDto;
import com.project.durumoongsil.teutoo.trainer.ptprogram.dto.request.PtProgramUpdateDto;
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

        // TraineInfo id 값만 존재하는 객체 획득
        TrainerInfo trainerInfo = this.getTrainerInfoForMapping();

        PtProgram ptProgram = ptProgramConverter.toPtProgram(ptProgramRegDto, trainerInfo);

        ptProgramRepository.save(ptProgram);

        if (ptProgramRegDto.getAddPtImgList() != null) {
            // pt 프로그램 이미지 저장
            savePtProgramImg(ptProgram, ptProgramRegDto.getAddPtImgList());
        }
    }

    private TrainerInfo getTrainerInfoForMapping() {
        String userEmail = securityService.getLoginedUserEmail();

        // trainer info id 만 조회,
        Long trainerInfoId = trainerInfoRepository.findTrainerInfoIdByMemberEmail(userEmail)
                .orElseThrow(TrainerInfoNotFoundException::new);

        return TrainerInfo.builder()
                .id(trainerInfoId)
                .build();
    }

    // PT Program 업데이트
    @Transactional
    public void update(Long programId, PtProgramUpdateDto ptProgramUpdateDto) {
        String userEmail = securityService.getLoginedUserEmail();

        // 프로그램과 이미지,파일 엔티티 한번에 조회
        PtProgram ptProgram = ptProgramRepository.findByIdAndMemberEmailWithPtImgAndFile(programId, userEmail)
                .orElseThrow(PtProgramNotFoundException::new);

        // PtProgram 프로퍼티 값 업데이트
        this.updatePtProgram(ptProgramUpdateDto, ptProgram);

        // 사용자가 삭제한 이미지가 존재한다면,
        if (ptProgramUpdateDto.getDelPtImgList() != null) {
            List<String> reqDelImgList = ptProgramUpdateDto.getDelPtImgList();

            // DB에 저장된 파일명만 필터링하여 획득
            List<PtImg> delPtImgList = ptProgram.getPtImgList().stream()
                    .filter(ptImg -> reqDelImgList.contains(ptImg.getFile().getFileName())).toList();

            deletePtProgramImg(delPtImgList);
        }

        // 자격사항 이미지 저장
        if (ptProgramUpdateDto.getAddPtImgList() != null) {
            savePtProgramImg(ptProgram, ptProgramUpdateDto.getAddPtImgList());
        }
    }

    private void updatePtProgram(PtProgramUpdateDto ptProgramUpdateDto, PtProgram ptProgram) {
        ptProgram.updatePrice(ptProgramUpdateDto.getPrice());
        ptProgram.updateTitle(ptProgramUpdateDto.getTitle());
        ptProgram.updateContent(ptProgramUpdateDto.getContent());
        ptProgram.updateAvailableStartTime(ptProgramUpdateDto.getAvailableStartTime());
        ptProgram.updateAvailableEndTime(ptProgramUpdateDto.getAvailableEndTime());

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

        Member member = this.getMember();

        List<PtProgramResDto> ptProgramResDtoList = this.getPtProgramListFromMember(member);

        // 사용자 프로필 이미지 생성
        ImgResDto imgResDto = ImgResDto.create(member.getProfileOriginalImageName(),
                    fileService.getImgUrl(FilePath.MEMBER_PROFILE.getPath(), member.getProfileImageName()));

        return ptProgramConverter.toPtProgramManageResDto(ptProgramResDtoList, member, imgResDto);
    }

    private Member getMember() {
        String memberEmail = securityService.getLoginedUserEmail();

        return memberRepository.findMemberByEmail(memberEmail)
                .orElseThrow(NotFoundUserException::new);
    }


    // PT 프로그램 리스트 조회
    public List<PtProgramResDto> getPtProgramListFromMember(Member member) {
        List<PtProgram> ptProgramList = ptProgramRepository.findByMemberEmailWithPtImg(member.getEmail());

        return ptProgramList
                .stream()
                .map(this::toPtProgramToPtProgramResDto)
                .toList();
    }

    private PtProgramResDto toPtProgramToPtProgramResDto(PtProgram ptProgram) {
        List<ImgResDto> imgResDtoList = this
                .toPtImgListToImgResDtoList(ptProgram.getPtImgList());
        return ptProgramConverter.toPtProgramResDto(ptProgram, imgResDtoList);
    }

    private List<ImgResDto> toPtImgListToImgResDtoList(List<PtImg> ptImgList) {

        return ptImgList.stream()
                .map(this::toPtImgToImgResDto)
                .toList();
    }

    private ImgResDto toPtImgToImgResDto(PtImg ptImg) {
        String fileName = ptImg.getFile().getFileName();
        String imgUrl = fileService.getImgUrl(ptImg.getFile().getFilePath(), fileName);
        return ImgResDto.create(fileName, imgUrl);
    }

    public PtProgramResDto getPtProgram(Long ptProgramId) {
        PtProgram ptProgram = ptProgramRepository
                .findByIdWithPtImgAndFile(ptProgramId).orElseThrow(PtProgramNotFoundException::new);

        Long trainerId = ptProgramRepository
                .findTrainerIdById(ptProgramId).orElseThrow(NotFoundUserException::new);

        List<ImgResDto> imgResDtoList =
                this.toPtImgListToImgResDtoList(ptProgram.getPtImgList());

        return PtProgramResDto
                .builder()
                .trainerId(trainerId)
                .ptProgramId(ptProgram.getId())
                .title(ptProgram.getTitle())
                .content(ptProgram.getContent())
                .price(ptProgram.getPrice())
                .ptProgramImgList(imgResDtoList)
                .build();
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
                .orElseThrow(PtProgramNotFoundException::new);

        // pt program 이미지 삭제 및 엔티티 삭제
        deletePtProgramImg(ptProgram.getPtImgList());

        ptProgramRepository.delete(ptProgram);
    }


}
