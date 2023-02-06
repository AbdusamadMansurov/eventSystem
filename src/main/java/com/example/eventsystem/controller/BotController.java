package com.example.eventsystem.controller;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.BotDTO;
import com.example.eventsystem.model.Bot;
import com.example.eventsystem.service.BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bot")
@RequiredArgsConstructor
public class BotController {
    private final BotService botService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page) {
        ApiResponse<Page<Bot>> response = botService.getAll(page);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        ApiResponse<Bot> response = botService.getOne(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/byCompany/{id}")
    public ResponseEntity<?> getAllByCompany(@PathVariable Long id) {
        ApiResponse<List<Bot>> response = botService.getAllByCompany(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<?> add(@ModelAttribute BotDTO dto) {
        ApiResponse<Bot> response = botService.add(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @ModelAttribute BotDTO dto) {
        ApiResponse<Bot> response = botService.edit(id, dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
