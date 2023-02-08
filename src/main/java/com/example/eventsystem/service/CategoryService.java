package com.example.eventsystem.service;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.model.Bot;
import com.example.eventsystem.model.Category;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.repository.BotRepository;
import com.example.eventsystem.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Malikov Azizjon  *  16.01.2023  *  17:52   *  IbratClub
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
//    @Value("${telegram.bot.id}")
//    private Long botId;
    private final CategoryRepository categoryRepository;
    private final BotRepository botRepository;

    public ApiResponse<List<Category>> getAll(Long botId, Employee employee) {
        Optional<Bot> botOptional = botRepository.findById(botId);
        if (botOptional.isEmpty() || !botOptional.get().getDepartment().getCompany().getId().equals(employee.getCompany().getId())) {
            return ApiResponse.<List<Category>>builder().
                    message("Not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        List<Category> categories = categoryRepository.findAllByDepartment_Bot_Id(botId);
        return ApiResponse.<List<Category>>builder().
                message("Here").
                status(200).
                success(true).
                data(categories).
                build();
    }

}
