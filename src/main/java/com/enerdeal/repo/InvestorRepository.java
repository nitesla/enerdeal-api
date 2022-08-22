package com.enerdeal.repo;

import com.enerdeal.model.Investor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvestorRepository extends JpaRepository<Investor, Long> {
    Investor findByUserId(Long id);

    List<Investor> findByIsActive(Boolean isActive);

    Investor findInvestorById(Long id);

    Investor findInvestorByUserId(Long id);

    @Query("SELECT p FROM Investor p WHERE ((:companyName IS NULL) OR (:companyName IS NOT NULL AND p.companyName like %:companyName%)) order by p.id desc")
    Page<Investor> findInvestorsProperties(String companyName, Pageable pageable);
}
