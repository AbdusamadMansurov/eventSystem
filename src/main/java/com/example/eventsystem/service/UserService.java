package com.example.eventsystem.service;

import com.example.eventsystem.dto.AddressDTO;
import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.UserDTO;
import com.example.eventsystem.model.*;
import com.example.eventsystem.model.enums.Gender;
import com.example.eventsystem.repository.*;
import com.example.eventsystem.specification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${page.size}")
    private int size;

    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final DepartmentRepository departmentRepository;
    private final CountryRepository countryRepository;

    public ApiResponse<Page<User>> getAll(boolean desc, int page, Employee employee, Boolean active) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, "id"));
        Page<User> users;

        if (active.equals(Boolean.TRUE)) {
            users = userRepository.findAllByActiveTrueAndDepartment_Company_Id(employee.getCompany().getId(), pageable);
        } else if (active.equals(Boolean.FALSE)) {
            users = userRepository.findAllByActiveFalseAndDepartment_Company_Id(employee.getCompany().getId(), pageable);
        } else {
            users = userRepository.findAllByDepartment_Company_Id(employee.getCompany().getId(), pageable);
        }
//        if (users.isEmpty()) {
//            return ApiResponse.<Page<User>>builder().
//                    success(false).
//                    status(400).
//                    message("Users not found").
//                    build();
//        }
        return ApiResponse.<Page<User>>builder().
                success(true).
                status(200).
                message("Users here").
                data(users).
                build();
    }

    public ApiResponse<User> getOne(Long id, Employee employee) {
        if (employee == null)
            return ApiResponse.<User>builder().
                    message("Employee not found!!!").
                    status(400).
                    success(false).
                    build();

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getDepartment().getCompany().getId().equals(employee.getCompany().getId()))
                return ApiResponse.<User>builder().
                        success(true).
                        status(200).
                        message("User here").
                        data(userOptional.get()).
                        build();
        }
        return ApiResponse.<User>builder().
                success(false).
                status(400).
                message("User is not found").
                build();
    }

    public ApiResponse<?> add(UserDTO dto, Employee employee) {
        User user = new User();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setPassportNumber(dto.getPassportNumber());
        user.setBrithDate(dto.getBrithDate());
        Optional<Department> departmentOptional = departmentRepository.findByIdAndCompany_Id(dto.getDepartmentId(), employee.getCompany().getId());
        if (departmentOptional.isEmpty()) {
            return ApiResponse.<User>builder().
                    message("Department not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        Department department = departmentOptional.get();
        user.setDepartment(department);
        if (dto.getPhone() != null && !dto.getPhone().equals("")) {
            Optional<User> userOptionalByPhone = userRepository.findByPhoneAndDepartment_Id(dto.getPhone(), department.getId());
            if (userOptionalByPhone.isPresent()) {
                return ApiResponse.<User>builder().
                        message("This phone number already exist!!!").
                        status(400).
                        success(false).
                        build();
            }
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null && !dto.getEmail().equals("")) {
            Optional<User> userOptionalByEmail = userRepository.findByEmailAndDepartment_Id(dto.getEmail(), department.getId());
            if (userOptionalByEmail.isPresent()) {
                return ApiResponse.<User>builder().
                        message("This email already exist!!!").
                        status(400).
                        success(false).
                        build();
            }
            user.setEmail(dto.getEmail());
        }
        try {
            user.setGender(Gender.valueOf(dto.getGenderType()));
        } catch (Exception e) {
            return ApiResponse.<User>builder().
                    message("Gender type not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        if (dto.getAddressDTO() != null) {
            Address address = new Address();
            AddressDTO addressDTO = dto.getAddressDTO();
            Optional<Country> countryOptional = countryRepository.findById(addressDTO.getCountryId());
            if (countryOptional.isEmpty()) {
                return ApiResponse.<User>builder().
                        message("Country not found!!!").
                        success(false).
                        status(400).
                        build();
            }
            Country country = countryOptional.get();
            address.setCountry(country);

            if (addressDTO.getRegionId() != null) {
                Optional<Region> regionOptional = regionRepository.findById(dto.getAddressDTO().getRegionId());
                if (regionOptional.isEmpty() || !regionOptional.get().getCountry().getId().equals(country.getId())) {
                    return ApiResponse.<User>builder().
                            message("Region not found!!!").
                            success(false).
                            status(400).
                            build();
                }
                address.setRegion(regionOptional.get());
            }
            if (addressDTO.getDistrictId() != null) {
                Optional<District> districtOptional = districtRepository.findById(addressDTO.getDistrictId());
                if (districtOptional.isEmpty()) {
                    return ApiResponse.<User>builder().
                            message("District not found!!!").
                            status(400).
                            success(false).
                            build();
                }
                address.setDistrict(districtOptional.get());
            }

            address.setStreetHome(address.getStreetHome());
            user.setAddress(address);
        }


        User save = userRepository.save(user);
        return ApiResponse.builder().
                message("User is created").
                status(201).
                success(true).
                data(save).
                build();
    }

    public ApiResponse<?> edit(Long id, UserDTO dto, Employee employee) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty() || !userOptional.get().getDepartment().getCompany().getId().equals(employee.getCompany().getId())) {
            return ApiResponse.builder().
                    success(false).
                    status(204).
                    message("User is not found").
                    build();
        }
        User user = userOptional.get();
        if (dto.getPhone() != null && !dto.getPhone().equals("")) {
            Optional<User> userOptionalByPhone = userRepository.findByPhoneAndDepartment_Id(dto.getPhone(), user.getDepartment().getId());
            if (userOptionalByPhone.isPresent() && !userOptionalByPhone.get().getId().equals(user.getId())) {
                return ApiResponse.<User>builder().
                        message("This phone number already exist!!!").
                        status(400).
                        success(false).
                        build();
            }
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null && !dto.getEmail().equals("")) {
            Optional<User> userOptionalByEmail = userRepository.findByEmailAndDepartment_Id(dto.getEmail(), user.getDepartment().getId());
            if (userOptionalByEmail.isPresent() && !userOptionalByEmail.get().getId().equals(user.getId())) {
                return ApiResponse.<User>builder().
                        message("This email already exist!!!").
                        status(400).
                        success(false).
                        build();
            }
            user.setEmail(dto.getEmail());
        }
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setPassportNumber(dto.getPassportNumber());
        user.setBrithDate(dto.getBrithDate());
        try {
            user.setGender(Gender.valueOf(dto.getGenderType()));
        } catch (IllegalArgumentException e) {
            return ApiResponse.<User>builder().
                    message("Gender type not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        if (dto.getAddressDTO() != null) {
            Address address = new Address();
            AddressDTO addressDTO = dto.getAddressDTO();
            Optional<Country> countryOptional = countryRepository.findById(addressDTO.getCountryId());
            if (countryOptional.isEmpty()) {
                return ApiResponse.<User>builder().
                        message("Country not found!!!").
                        success(false).
                        status(400).
                        build();
            }
            Country country = countryOptional.get();
            address.setCountry(country);

            if (addressDTO.getRegionId() != null) {
                Optional<Region> regionOptional = regionRepository.findById(dto.getAddressDTO().getRegionId());
                if (regionOptional.isEmpty() || !regionOptional.get().getCountry().getId().equals(country.getId())) {
                    return ApiResponse.<User>builder().
                            message("Region not found!!!").
                            success(false).
                            status(400).
                            build();
                }
                address.setRegion(regionOptional.get());
            }
            if (addressDTO.getDistrictId() != null) {
                Optional<District> districtOptional = districtRepository.findById(addressDTO.getDistrictId());
                if (districtOptional.isEmpty()) {
                    return ApiResponse.<User>builder().
                            message("District not found!!!").
                            status(400).
                            success(false).
                            build();
                }
                address.setDistrict(districtOptional.get());
            }

            address.setStreetHome(address.getStreetHome());
            user.setAddress(address);
        }
        userRepository.save(user);
        return ApiResponse.builder().
                message("User is edited!").
                status(200).
                success(true).
                build();
    }

    public ApiResponse<?> delete(Long id, Employee employee) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty() || !userOptional.get().getDepartment().getCompany().getId().equals(employee.getCompany().getId())) {
            return ApiResponse.builder().
                    message("User is not found !").
                    success(false).
                    status(400).
                    build();
        }

        User user = userOptional.get();
        user.setActive(!user.isActive());
        userRepository.save(user);
        return ApiResponse.builder().
                message("Is deleted !").
                success(true).
                status(200).
                build();
    }

    public ApiResponse<List<User>> getAllList() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            return ApiResponse.<List<User>>builder().
                    message("Users are not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        return ApiResponse.<List<User>>builder().
                message("Users here!!!").
                status(200).
                success(true).
                data(users).
                build();
    }


    public ApiResponse<List<User>> getByPhoneOrName(int page, String name, String phone, Employee employee) {
        SearchRequest searchRequest = new SearchRequest();
        List<FilterRequest> filterRequests = new ArrayList<>();
        filterRequests.add(FilterRequest.builder().
                fieldType(FieldType.LONG).
                operator(Operator.EQUAL).
                value(employee.getCompany().getId()).
                key("department.company.id").
                build());
        if (name != null){
            filterRequests.add(FilterRequest.builder().
                    fieldType(FieldType.STRING).
                    operator(Operator.LIKE).
                    value(name).
                    key("fullName").
                    build());
        }
        if (phone != null){
            filterRequests.add(FilterRequest.builder().
                    fieldType(FieldType.STRING).
                    operator(Operator.LIKE).
                    value(phone).
                    key("phone").
                    build());
        }

        List<User> userList = userRepository.findAll(new EntitySpecification<>(searchRequest));
           return ApiResponse.<List<User>>builder().
                message("Users here!!!").
                status(200).
                success(true).
                data(userList).
                build();
    }
}
