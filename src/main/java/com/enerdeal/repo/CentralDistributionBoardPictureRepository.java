package com.enerdeal.repo;

import com.enerdeal.model.CentralDistributionBoardPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CentralDistributionBoardPictureRepository extends JpaRepository<CentralDistributionBoardPicture, Long> {
    List<CentralDistributionBoardPicture> findByProjectId(long projectId);

    void deleteAllByProjectId(long projectId);
}
