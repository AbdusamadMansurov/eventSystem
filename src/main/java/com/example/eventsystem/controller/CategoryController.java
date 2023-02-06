package com.example.eventsystem.controller;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.model.Category;
import com.example.eventsystem.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Malikov Azizjon  *  16.01.2023  *  17:53   *  IbratClub
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;
    @GetMapping
    public ResponseEntity<?> getAll() {
        ApiResponse<List<Category>> response = categoryService.getAll();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
