package com.example.eventsystem.service;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.model.Request;
import com.example.eventsystem.repository.ProductRepository;
import com.example.eventsystem.repository.RequestRepository;
import com.example.eventsystem.repository.SiteHistoryRepository;
import com.example.eventsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {
    @Value("${page.size}")
    private int size;
    private final RequestRepository requestRepository;
    private final SiteHistoryRepository siteHistoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TelegramService telegramService;

    public ApiResponse<Page<Request>> getRequest(int page, Boolean view) {


        Pageable pageable = PageRequest.of(page, size);
        Page<Request> requests;

        if (view == null)
            requests = requestRepository.findAll(pageable);
        else
            requests = requestRepository.findAllByView(view, pageable);

        if (requests.isEmpty()) {
            return ApiResponse.<Page<Request>>builder().
                    message("Requests not found !").
                    status(400).
                    success(false).
                    build();
        }
        return ApiResponse.<Page<Request>>builder().
                message("Requests here !").
                status(200).
                success(true).
                data(requests).
                build();
    }

    public ApiResponse<Request> getOneRequest(Long id) {
        Optional<Request> requestOptional = requestRepository.findById(id);

        if (requestOptional.isEmpty()) {
            return ApiResponse.<Request>builder().
                    message("Requests not found !").
                    status(400).
                    success(false).
                    build();
        }
        return ApiResponse.<Request>builder().
                message("Requests here !").
                status(200).
                success(true).
                data(requestOptional.get()).
                build();
    }
}
