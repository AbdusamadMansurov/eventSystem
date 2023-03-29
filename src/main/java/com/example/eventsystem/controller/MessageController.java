package com.example.eventsystem.controller;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.MessageDTO;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.model.Message;
import com.example.eventsystem.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(messageService.getAll());
    }

    @PutMapping
    public ResponseEntity<?> edit(@RequestBody List<Long> list) {
        return ResponseEntity.ok(messageService.editStatus(list));
    }

    @GetMapping("/employee")
    public ResponseEntity<?> getAllByEmployee(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal Employee employee) {
        ApiResponse<Page<Message>> allByEmployee = messageService.getAllByEmployee(employee, page, size);
        return ResponseEntity.status(allByEmployee.getStatus()).body(allByEmployee);
    }

    @PostMapping
    public ResponseEntity<?> add(
            @RequestBody MessageDTO messageDTO,
            @RequestBody Employee employee
    ) {
        ApiResponse<?> add = messageService.add(messageDTO, employee);
        return ResponseEntity.status(add.getStatus()).body(add);
    }
}
