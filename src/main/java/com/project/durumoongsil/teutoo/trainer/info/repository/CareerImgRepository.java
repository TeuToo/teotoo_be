package com.project.durumoongsil.teutoo.trainer.info.repository;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerImgRepository extends JpaRepository<CareerImg, Long> {

    @Query("select ci from CareerImg ci join fetch ci.file where ci.trainerInfo.id = :trainerId")
    List<CareerImg> findByTrainerIdWithFile(@Param("trainerId") Long trainerId);

    @Query("select ci from CareerImg ci inner join ci.trainerInfo inner join fetch ci.file where ci.trainerInfo.id = :trainerInfoId and ci.file.fileName in :fileNameList")
    List<CareerImg> findByFileNameWithCareerImg(@Param("trainerInfoId") Long trainerInfoId, @Param("fileNameList") List<String> fileNameList);

    @Modifying
    @Query("delete from CareerImg c where c.id in :idList")
    void deleteAllById(@Param("idList") List<Long> idList);
}
