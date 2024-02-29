package com.project.durumoongsil.teutoo.trainer.info.repository;

import com.project.durumoongsil.teutoo.common.domain.File;
import com.project.durumoongsil.teutoo.trainer.info.domain.CareerImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerImgRepository extends JpaRepository<CareerImg, Long> {

    @Query("select ci from CareerImg ci join fetch ci.file where ci.trainerInfo.id = :trainerId")
    List<CareerImg> findByTrainerIdWithFile(@Param("trainerId") Long trainerId);

    @Query("select f from File f left join fetch f.careerImg where f.fileName in :fileName")
    List<CareerImg> findByFileNameWithCareerImg(@Param("fileNameList") List<String> fileName);
}
