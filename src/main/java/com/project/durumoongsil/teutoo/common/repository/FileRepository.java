package com.project.durumoongsil.teutoo.common.repository;

import com.project.durumoongsil.teutoo.common.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {


    @Query("select f from File f left join fetch f.careerImg where f.fileName in :fileName")
    List<File> findByFileNameWithCareerImg(@Param("fileNameList") List<String> fileName);

    Optional<File> deleteByFileName(String fileName);
}
