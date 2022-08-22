package com.enerdeal.repo;

import com.enerdeal.model.BuildingPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingPictureRepository extends JpaRepository<BuildingPicture, Long> {
    List<BuildingPicture> findByProjectId(long projectId);

    void deleteAllByProjectId(long projectId);
}
