package com.enerdeal.repo;

import com.enerdeal.model.GeneratorPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneratorPictureRepository extends JpaRepository<GeneratorPicture, Long> {
    List<GeneratorPicture> findByProjectId(long projectId);
    void deleteAllByProjectId(long projectId);
}
