package com.example.eventsystem.service;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.model.Product;
import com.example.eventsystem.model.Request;
import com.example.eventsystem.model.User;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public ApiResponse<?> edit(Long eventId, String qrcode, Employee employee) {
        Optional<Product> productOptional = productRepository.findById(eventId);
        if (productOptional.isEmpty() || !productOptional.get().getCategory().getDepartment().getCompany().getId().equals(employee.getCompany().getId())) {
            return ApiResponse.builder()
                    .message("Event not found ")
                    .status(400)
                    .success(false)
                    .build();
        }
        Optional<User> userOptional = userRepository.findByQrcode(UUID.fromString(qrcode));
        if (userOptional.isEmpty()) {
            return ApiResponse.builder()
                    .message("User not found ")
                    .status(400)
                    .success(false)
                    .build();
        }
        List<Request> requestList = requestRepository.findAllByProductAndUser(productOptional.get(), userOptional.get());
        if (requestList.isEmpty()) {
            return ApiResponse.builder()
                    .message("User is not registered")
                    .status(400)
                    .success(false)
                    .build();
        }
        Request request = requestList.get(0);
        if (request.getArrivalTime() != null) {
            return ApiResponse.builder()
                    .message("User is registered")
                    .status(200)
                    .success(true)
                    .data(request.getUser())
                    .build();
        }
        request.setArrivalTime(LocalDateTime.now());
        requestRepository.save(request);
        return ApiResponse.builder()
                .message("Success ")
                .status(200)
                .success(true)
                .data(request.getUser())
                .build();
    }
}
