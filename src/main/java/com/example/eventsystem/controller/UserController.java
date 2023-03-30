package com.example.eventsystem.controller;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.UserDTO;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.model.User;
import com.example.eventsystem.repository.UserRepository;
import com.example.eventsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAllByCompany(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "false") boolean desc,
                                             @RequestParam(defaultValue = "registeredTime") String sortBy,
                                             @AuthenticationPrincipal Employee employee,
                                             @RequestParam(defaultValue = "null") Boolean active) {
        ApiResponse<Page<User>> response = userService.getAll(desc, sortBy, page, employee, active);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/getByQ")
    public ResponseEntity<?> getByPhoneOrName(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(required = false) String name,
                                              @RequestParam(required = false) String phone,
                                              @AuthenticationPrincipal Employee employee) {
        ApiResponse<List<User>> response = userService.getByPhoneOrName(page, name, phone, employee);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id, @AuthenticationPrincipal Employee employee) {
        ApiResponse<User> response = userService.getOne(id, employee);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLIst() {
        ApiResponse<List<User>> response = userService.getAllList();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody UserDTO dto, @AuthenticationPrincipal Employee employee) {
        ApiResponse<?> response = userService.add(dto, employee);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody UserDTO dto, @AuthenticationPrincipal Employee employee) {
        ApiResponse<?> response = userService.edit(id, dto, employee);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestParam Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message("Not authorization")
                    .build());
        }
        return ResponseEntity.ok().body(userOptional.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @AuthenticationPrincipal Employee employee) {
        ApiResponse<?> response = userService.delete(id, employee);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}

