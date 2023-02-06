package com.example.eventsystem.repository;

import com.example.eventsystem.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * @author Mansurov Abdusamad  *  30.11.2022  *  10:08   *  tedaSystem
 */
@Component
@Repository
public interface DistrictRepository extends JpaRepository<District,Long> {
}
