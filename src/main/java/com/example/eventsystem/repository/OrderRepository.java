package com.example.eventsystem.repository;

import com.example.eventsystem.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Mansurov Abdusamad  *  30.11.2022  *  13:38   *  tedaSystem
 */

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByClient_Company_IdAndActiveTrueAndReadyTrue(Long companyId, Pageable pageable);

    Page<Order> findAllByClient_Company_IdAndActiveTrueAndReadyFalse(Long companyId, Pageable pageable);

    Page<Order> findAllByClient_Company_IdAndActiveFalseAndReadyTrue(Long companyId, Pageable pageable);

    Page<Order> findAllByClient_Company_IdAndActiveFalseAndReadyFalse(Long companyId, Pageable pageable);


}
