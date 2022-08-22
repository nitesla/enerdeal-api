package com.enerdeal.repo;

import com.enerdeal.model.PreviousPasswords;
import com.enerdeal.model.ProjectDocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDocumentsRepository extends JpaRepository<ProjectDocuments, Long> {


    ProjectDocuments findByProjectIdAndProjectFileNameAndProjectfileType(Long projectId,String projectFileName,String projectfileType);

    List<ProjectDocuments> findByProjectId(Long projectId);

    List<ProjectDocuments> findByProjectIdAndProjectFileName(Long projectId,String projectFileName);
}
