package com.example.eventsystem.service;

import com.example.eventsystem.dto.AddressDTO;
import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.EmployeeDTO;
import com.example.eventsystem.model.*;
import com.example.eventsystem.model.enums.RoleType;
import com.example.eventsystem.repository.CompanyRepository;
import com.example.eventsystem.repository.DistrictRepository;
import com.example.eventsystem.repository.EmployeeRepository;
import com.example.eventsystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Mansurov Abdusamad  *  12.12.2022  *  10:31   *  tedaSystem
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {
    @Value("${page.size}")
    private int size;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;
    private final DistrictRepository districtRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;

    public ApiResponse<Page<Employee>> getAll(int page, Boolean active, Employee employee) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage;
        if (Boolean.TRUE.equals(active))
            employeePage = employeeRepository.findAllByActiveTrueAndCompany_Id(employee.getCompany().getId(), pageable);
        else if (Boolean.FALSE.equals(active))
            employeePage = employeeRepository.findAllByActiveFalseAndCompany_Id(employee.getCompany().getId(), pageable);
        else
            employeePage = employeeRepository.findAllByCompany_Id(employee.getCompany().getId(),pageable);

        if (employeePage.isEmpty())
            return ApiResponse.<Page<Employee>>builder().
                    message("Employees not found!!!").
                    success(false).
                    status(400).
                    build();
        return ApiResponse.<Page<Employee>>builder().
                message("Employees here!!!").
                success(true).
                status(200).
                data(employeePage).
                build();

    }

    public ApiResponse<Employee> getOne(Long id, Employee employee) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (employeeOptional.isEmpty() || !employeeOptional.get().getCompany().getId().equals(employee.getCompany().getId())) {
            return ApiResponse.<Employee>builder().
                    message("Employee not found!!!").
                    success(false).
                    status(400).
                    build();
        }
        return ApiResponse.<Employee>builder().
                message("Employees here!!!").
                success(true).
                status(200).
                data(employeeOptional.get()).
                build();

    }

    public ApiResponse<Employee> add(EmployeeDTO dto, Employee employee1) {
        Optional<Company> companyOptional = companyRepository.findById(employee1.getCompany().getId());
        if (companyOptional.isEmpty()) {
            return ApiResponse.<Employee>builder().
                    message("Company not found!!!").
                    success(false).
                    status(400).
                    build();
        }

        if (employeeRepository.findByPhoneFirstAndCompany_Id(dto.getPhoneFirst(), employee1.getCompany().getId()).isPresent()) {
            return ApiResponse.<Employee>builder().
                    message("This phone number " + dto.getPhoneFirst() + " is used. Please enter another phone number").
                    success(false).
                    status(400).
                    build();
        }
        if (employeeRepository.findByUsername(dto.getUsername()).isPresent()) {
            return ApiResponse.<Employee>builder().
                    message("This username " + dto.getUsername() + " is used. Please enter another username").
                    success(false).
                    status(400).
                    build();
        }

        Employee employee = new Employee();
        if (dto.getAddressDTO() != null) {
            AddressDTO addressDTO = dto.getAddressDTO();
            Address address = new Address();

            Optional<District> districtOptional = districtRepository.findById(addressDTO.getDistrictId());
            if (districtOptional.isEmpty()) {
                return ApiResponse.<Employee>builder().
                        message("District not found!!!").
                        success(false).
                        status(400).
                        build();
            }
            address.setDistrict(districtOptional.get());
            address.setStreetHome(address.getStreetHome());
            employee.setAddress(address);
        }
        employee.setCompany(companyOptional.get());
        employee.setFullName(dto.getFullName());
        employee.setPhoneFirst(dto.getPhoneFirst());
        employee.setPhoneSecond(dto.getPhoneSecond());
        employee.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<RoleType> roles = new HashSet<>();

        for (String roleString : dto.getRoleList()) {
            roles.add(RoleType.valueOf(roleString));
        }

        employee.setRoles(roles);
        Employee save = employeeRepository.save(employee);

        return ApiResponse.<Employee>builder().
                message("Employee saved!!!").
                success(true).
                status(201).
                data(save).
                build();
    }

    public ApiResponse<Employee> edit(Long id, EmployeeDTO dto) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (employeeOptional.isEmpty()) {
            return ApiResponse.<Employee>builder().
                    message("Employee not found!!!").
                    success(false).
                    status(400).
                    build();
        }

        Employee employee = employeeOptional.get();

        Optional<Employee> byPhoneFirst = employeeRepository.findByPhoneFirstAndCompany_Id(dto.getPhoneFirst(), employee.getCompany().getId());

        if (byPhoneFirst.isPresent()) {
            Employee employee1 = byPhoneFirst.get();
            if (employee1.getId().equals(employee.getId()))
                return ApiResponse.<Employee>builder().
                        message("This phone number is used another employee. Please enter another phone number").
                        success(false).
                        status(400).
                        build();
        }

        employee.setPhoneFirst(dto.getPhoneFirst());
        employee.setPhoneSecond(dto.getPhoneSecond());
        employee.setFullName(dto.getFullName());
        employee.setPassword(passwordEncoder.encode(dto.getPassword()));

        Set<RoleType> roles = new HashSet<>();

        for (String roleString : dto.getRoleList()) {
            roles.add(RoleType.valueOf(roleString));
        }

        employee.setRoles(roles);

        if (dto.getAddressDTO() != null) {
            Address address = new Address();
            AddressDTO addressDTO = dto.getAddressDTO();
            if (employee.getAddress() != null)
                address = employee.getAddress();
            Optional<District> districtOptional = districtRepository.findById(addressDTO.getDistrictId());
            if (districtOptional.isEmpty()) {
                return ApiResponse.<Employee>builder().
                        message("District not found!!!").
                        success(false).
                        status(400).
                        build();
            }
            address.setDistrict(districtOptional.get());
            address.setStreetHome(address.getStreetHome());
            employee.setAddress(address);
        }

        Employee save = employeeRepository.save(employee);

        return ApiResponse.<Employee>builder().
                message("Employee edited!!!").
                success(true).
                status(200).
                data(save).
                build();
    }

    public ApiResponse<?> delete(Long id, Employee employee) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty() || !employeeOptional.get().getCompany().getId().equals(employee.getCompany().getId())) {
            return ApiResponse.builder().
                    message("Employee not found!!!").
                    success(false).
                    status(400).
                    build();
        }
        Employee employee1 = employeeOptional.get();
        employee1.setActive(!employee1.isActive());
        employeeRepository.save(employee1);

        return ApiResponse.builder().
                message("Employee status is edited!!!").
                success(true).
                status(200).
                build();
    }

    public ApiResponse<Page<Employee>> getAllByCompany(int page, Boolean active, Employee employee) {
        Long companyId = employee.getCompany().getId();
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if (companyOptional.isEmpty()) {
            return ApiResponse.<Page<Employee>>builder().
                    message("Company not found!!!").
                    success(false).
                    status(400).
                    build();
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employeePage;

        if (active.equals(Boolean.TRUE))
            employeePage = employeeRepository.findAllByActiveTrueAndCompany_Id(companyId, pageable);
       else if (active.equals(Boolean.FALSE))
            employeePage = employeeRepository.findAllByActiveFalseAndCompany_Id(companyId, pageable);
        else
            employeePage = employeeRepository.findAllByCompany_Id(companyId, pageable);

        if (employeePage.isEmpty()) {
            return ApiResponse.<Page<Employee>>builder().
                    message("Employees not found!!!").
                    success(false).
                    status(400).
                    build();
        }

        return ApiResponse.<Page<Employee>>builder().
                message("Employees here!!!").
                success(true).
                status(200).
                data(employeePage).
                build();
    }
    public ApiResponse<?> editEvent(Long id, Employee employee) {
        Optional<Product> productOptional = productRepository.findByIdAndCategory_Department_Company_Id(id, employee.getCompany().getId());
        if (productOptional.isEmpty()){
            return ApiResponse.builder().
                    message("Event not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        employee.setProduct(productOptional.get());
        return ApiResponse.builder().
                message("Success edited!!!").
                status(200).
                success(true).
                data(employeeRepository.save(employee)).
                build();
    }
}
