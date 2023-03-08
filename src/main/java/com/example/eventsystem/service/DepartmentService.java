package com.example.eventsystem.service;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.model.Department;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public ApiResponse<List<Department>> getAll(Employee employee) {
        List<Department> departmentList = departmentRepository.findAllByCompany_Id(employee.getCompany().getId());
        return ApiResponse.<List<Department>>builder().
                message("Here").
                status(200).
                success(true).
                data(departmentList).
                build();
    }
}
