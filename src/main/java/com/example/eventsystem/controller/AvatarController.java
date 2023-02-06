package com.example.eventsystem.controller;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.AvatarDTO;
import com.example.eventsystem.model.Avatar;
import com.example.eventsystem.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mansurov Abdusamad  *  07.12.2022  *  12:06   *  tedaSystem
 */
@RestController
@RequestMapping("/avatar")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService avatarService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page){
        ApiResponse<Page<Avatar>> response = avatarService.getAll(page);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id){
        ApiResponse<Avatar> response = avatarService.getOne(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody AvatarDTO dto){
        ApiResponse<Avatar> response = avatarService.add(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id, @RequestBody AvatarDTO avatarDTO){
        ApiResponse<Avatar> response = avatarService.edit(id, avatarDTO);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
