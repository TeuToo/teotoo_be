package com.project.durumoongsil.teutoo.trainer.repository;

import com.project.durumoongsil.teutoo.common.domain.QFile;
import com.project.durumoongsil.teutoo.trainer.domain.CareerImg;
import com.project.durumoongsil.teutoo.trainer.domain.QCareerImg;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CareerImgCustomRepositoryImpl implements CareerImgCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    @Override
    public List<CareerImg> findByTrainerIdWithFile(Long trainerId) {

        QCareerImg qCareerImg = QCareerImg.careerImg;
        QFile qFile = QFile.file;

        return queryFactory.selectFrom(qCareerImg)
                    .join(qCareerImg.file, qFile).fetchJoin()
                    .where(qCareerImg.trainerInfo.id.eq(trainerId))
                    .fetch();
    }
}
