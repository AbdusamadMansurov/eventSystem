package com.example.eventsystem.repository;

import com.example.eventsystem.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Mansurov Abdusamad  *  30.11.2022  *  10:08   *  tedaSystem
 */

@Repository
public interface CountryRepository extends JpaRepository<Country,Long> {
}
