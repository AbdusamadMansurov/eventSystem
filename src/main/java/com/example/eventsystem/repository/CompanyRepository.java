package com.example.eventsystem.repository;

import com.example.eventsystem.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Malikov Azizjon    ourSystem    26.12.2022    15:40
 */

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Page<Company> findAllByActive(Pageable pageable, Company company, boolean active);

}
