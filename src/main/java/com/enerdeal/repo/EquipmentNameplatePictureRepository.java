package com.enerdeal.repo;

import com.enerdeal.model.EquipmentNameplatePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentNameplatePictureRepository extends JpaRepository<EquipmentNameplatePicture, Long> {
    List<EquipmentNameplatePicture> findByProjectId(long projectId);
    void deleteAllByProjectId(long projectId);
}
