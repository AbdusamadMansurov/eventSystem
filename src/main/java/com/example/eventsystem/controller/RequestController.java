package com.example.eventsystem.controller;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.RequestDTO;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.model.Request;
import com.example.eventsystem.model.SiteHistory;
import com.example.eventsystem.service.RequestService;
import com.example.eventsystem.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author Mansurov Abdusamad  *  30.11.2022  *  10:35   *  tedaSystem
 */

@RestController
@RequestMapping("/api/site")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final SiteService siteService;


    @GetMapping("/getRequests")
    public ResponseEntity<?> getRequest(@RequestParam(defaultValue = "0") int page, @RequestParam Boolean view) {
        ApiResponse<Page<Request>> response = siteService.getRequest(page, view);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/getHistory")
    public ResponseEntity<?> getSiteHistory(@RequestParam(defaultValue = "0") int page) {
        ApiResponse<Page<SiteHistory>> response = siteService.getSiteHistory(page);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/getRequest/{id}")
    public ResponseEntity<?> getOneRequest(@PathVariable Long id){
        ApiResponse<Request> response = siteService.getOneRequest(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/getHistory/{id}")
    public ResponseEntity<?> getOneSiteHistory(@PathVariable Long id) {
        ApiResponse<SiteHistory> response = siteService.getOneSiteHistory(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PatchMapping("/request/{id}")
    public ResponseEntity<?> editRequestStatus(@PathVariable Long id, @AuthenticationPrincipal Employee employee){
        ApiResponse<Request> response = siteService.editRequestStatus(id, employee);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public ResponseEntity<?> addRequest(@RequestBody RequestDTO dto){
        ApiResponse<Request> response = siteService.addRequest(dto);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @PatchMapping("/{eventId}")
    public ResponseEntity<?> edit(@PathVariable Long eventId, @RequestParam String qrcode, @AuthenticationPrincipal Employee employee){
        ApiResponse<?> response = requestService.edit(eventId, qrcode, employee);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
