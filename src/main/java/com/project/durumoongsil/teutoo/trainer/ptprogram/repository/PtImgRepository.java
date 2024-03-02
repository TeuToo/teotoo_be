package com.project.durumoongsil.teutoo.trainer.ptprogram.repository;

import com.project.durumoongsil.teutoo.trainer.ptprogram.domain.PtImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PtImgRepository extends JpaRepository<PtImg, Long> {

    @Modifying
    @Query("delete from PtImg pi where pi.id in :idList")
    void deleteAllById(@Param("idList") List<Long> idList);

    @Query("select pi from PtImg pi " +
            "inner join pi.ptProgram " +
            "inner join fetch pi.file " +
            "where pi.ptProgram.id = :ptProgramId and pi.file.fileName in :imgNameList")
    List<PtImg> findAllByProgramIdAndImgNameListWithFile(@Param("ptProgramId")Long ptProgramId, @Param("imgNameList")List<String> imgNameList);
}
