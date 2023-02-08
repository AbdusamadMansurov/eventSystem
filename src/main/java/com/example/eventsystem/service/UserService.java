package com.example.eventsystem.service;

import com.example.eventsystem.dto.AddressDTO;
import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.UserDTO;
import com.example.eventsystem.model.Address;
import com.example.eventsystem.model.District;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.model.User;
import com.example.eventsystem.model.enums.Gender;
import com.example.eventsystem.repository.DistrictRepository;
import com.example.eventsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Value("${page.size}")
    private int size;

    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;

    public ApiResponse<Page<User>> getAll(int page, Employee employee, Boolean active) {

        Pageable pageable = PageRequest.of(page, size);
        if (employee == null)
            return ApiResponse.<Page<User>>builder().
                    message("Employee not found!!!").
                    status(400).
                    success(false).
                    build();

        Page<User> users = userRepository.findAllByActiveAndCompany(pageable, employee.getCompany(), active);
        if (users.isEmpty()) {
            return ApiResponse.<Page<User>>builder().
                    success(false).
                    status(400).
                    message("Users not found").
                    build();
        }
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
        user.setEmail(dto.getEmail());
        user.setPassportNumber(dto.getPassportNumber());
        user.setBrithDate(dto.getBrithDate());
        try {
            user.setGender(Gender.valueOf(dto.getGenderType()));
        } catch (Exception e) {
            return ApiResponse.<User>builder().
                    message("Gender type not found!!!").
                    status(400).
                    success(false).
                    build();
        }

        Address address = new Address();

        AddressDTO addressDTO = dto.getAddressDTO();

        Optional<District> districtOptional = districtRepository.findById(addressDTO.getDistrictId());

        if (districtOptional.isEmpty()) {
            return ApiResponse.<User>builder().
                    message("District not found!!!").
                    status(400).
                    success(false).
                    build();
        }

        address.setDistrict(districtOptional.get());
        address.setStreetHome(address.getStreetHome());

        user.setAddress(address);

        userRepository.save(user);
        return ApiResponse.builder().
                message("User is created").
                status(201).
                success(true).
                build();
    }

    public ApiResponse<?> edit(Long id, UserDTO dto, Employee employee) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return ApiResponse.builder().
                    success(false).
                    status(204).
                    message("User is not found").
                    build();
        }
        User user = userOptional.get();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
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
        Address address = user.getAddress();
        AddressDTO addressDTO = dto.getAddressDTO();
        Optional<District> districtOptional = districtRepository.findById(addressDTO.getDistrictId());

        if (districtOptional.isEmpty()) {
            return ApiResponse.<User>builder().
                    message("District not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        address.setDistrict(districtOptional.get());
        address.setStreetHome(address.getStreetHome());
        user.setAddress(address);
        userRepository.save(user);
        return ApiResponse.builder().
                message("User is edited!").
                status(200).
                success(true).
                build();
    }

    public ApiResponse<?> delete(Long id, Employee employee) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
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

}
