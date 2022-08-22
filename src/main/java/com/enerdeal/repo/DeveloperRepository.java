package com.enerdeal.repo;


import com.enerdeal.model.Developer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DeveloperRepository extends JpaRepository<Developer, Long> {

    Developer findByUserId(Long userId);

    Developer findDeveloperById(Long id);

    Developer findDeveloperByUserId(Long id);

    List<Developer> findByIsActive(Boolean isActive);

    @Query("SELECT p FROM Developer p WHERE ((:companyName IS NULL) OR (:companyName IS NOT NULL AND p.companyName like %:companyName%)) order by p.id desc")
    Page<Developer> findDevelopersProperties(@Param("companyName") String companyName, Pageable pageable);

}
