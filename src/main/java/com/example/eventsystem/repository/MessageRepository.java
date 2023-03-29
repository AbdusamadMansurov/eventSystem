package com.example.eventsystem.repository;

import com.example.eventsystem.model.Employee;
import com.example.eventsystem.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByEmployeeAndSendTime(Employee employee, LocalDateTime time, Pageable pageable);
    Page<Message> findAllByEmployee(Employee employee, Pageable pageable);
}