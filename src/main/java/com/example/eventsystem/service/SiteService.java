package com.example.eventsystem.service;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.RequestDTO;
import com.example.eventsystem.model.*;
import com.example.eventsystem.model.enums.RequestType;
import com.example.eventsystem.repository.ProductRepository;
import com.example.eventsystem.repository.RequestRepository;
import com.example.eventsystem.repository.SiteHistoryRepository;
import com.example.eventsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Mansurov Abdusamad  *  24.11.2022  *  10:28   *  tedaUz
 */
@Service
@RequiredArgsConstructor
public class SiteService {
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

    public ApiResponse<Page<SiteHistory>> getSiteHistory(int page) {

        Pageable pageable = PageRequest.of(page, size);

        Page<SiteHistory> siteHistories = siteHistoryRepository.findAll(pageable);
        if (siteHistories.isEmpty()) {
            return ApiResponse.<Page<SiteHistory>>builder().
                    message("History not found !").
                    status(400).
                    success(false).
                    build();
        }
        return ApiResponse.<Page<SiteHistory>>builder().
                message("History here !").
                status(200).
                success(true).
                data(siteHistories).
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

    public ApiResponse<SiteHistory> getOneSiteHistory(Long id) {
        Optional<SiteHistory> historyOptional = siteHistoryRepository.findById(id);

        if (historyOptional.isEmpty()) {
            return ApiResponse.<SiteHistory>builder().
                    message("History not found !").
                    status(400).
                    success(false).
                    build();
        }

        return ApiResponse.<SiteHistory>builder().
                message("History not found !").
                status(400).
                success(false).
                data(historyOptional.get()).
                build();
    }

    @SneakyThrows
    public ApiResponse<Request> editRequestStatus(Long id, Employee employee) {
        Optional<Request> requestOptional = requestRepository.findById(id);
        if (requestOptional.isEmpty()) {
            return ApiResponse.<Request>builder().
                    message("Request id not found !").
                    status(400).
                    success(false).
                    build();
        }

        if (employee == null)
            return ApiResponse.<Request>builder().
                    message("Employee not found !").
                    status(400).
                    success(false).
                    build();
        Request request = requestOptional.get();
        request.setView(true);
        request.setEmployee(employee);
        if (request.getUser().getChatId() != null) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Sizning " + request.getId() + " raqamli murojatingizni " + request.getEmployee().getFullName() + " qabul qildi.");
            sendMessage.setChatId(request.getUser().getChatId());
            telegramService.execute(sendMessage);
        }
        request.setArrivalTime(LocalDateTime.now());
        requestRepository.save(request);
        return ApiResponse.<Request>builder().
                message("Sent!!!").
                success(true).
                status(200).
                build();

    }

    public ApiResponse<Request> addRequest(RequestDTO dto) {
        Optional<User> userOptional = userRepository.findById(dto.getUserId());

        if (userOptional.isEmpty()) {
            return ApiResponse.<Request>builder().
                    message("User not found!!!").
                    success(false).
                    status(400).
                    build();
        }
        Request request = new Request();
        Optional<Product> productOptional = productRepository.findById(dto.getProductId());
        productOptional.ifPresent(request::setProduct);

        request.setRequestStatusType(RequestType.UNDER_REVIEW);
        request.setUser(userOptional.get());
        request.setAboutProduct(dto.getAboutProduct());
        request.setCategory(dto.getCategory());
        Request save = requestRepository.save(request);

        return ApiResponse.<Request>builder().
                message("Request accepted!!!").
                success(true).
                status(201).
                data(save).
                build();
    }
}
