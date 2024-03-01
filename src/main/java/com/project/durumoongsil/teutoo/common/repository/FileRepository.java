package com.project.durumoongsil.teutoo.common.repository;

import com.project.durumoongsil.teutoo.common.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    @Modifying
    @Query("delete from File f where f.fileName in :fileNameList")
    void deleteAllByFileName(@Param("fileNameList") List<String> fileNameList);
}
