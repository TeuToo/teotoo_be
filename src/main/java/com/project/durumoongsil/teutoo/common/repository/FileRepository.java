package com.project.durumoongsil.teutoo.common.repository;

import com.project.durumoongsil.teutoo.common.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

}
