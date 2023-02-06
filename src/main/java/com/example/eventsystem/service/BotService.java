package com.example.eventsystem.service;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.BotDTO;
import com.example.eventsystem.model.Attachment;
import com.example.eventsystem.model.Bot;
import com.example.eventsystem.model.Company;
import com.example.eventsystem.repository.BotRepository;
import com.example.eventsystem.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotService {
    @Value("${page.size}")
    private int size;
    private final BotRepository botRepository;
    private final CompanyRepository companyRepository;

    public ApiResponse<Page<Bot>> getAll(int page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bot> botPage = botRepository.findAll(pageable);
        if (botPage.isEmpty())
            return ApiResponse.<Page<Bot>>builder().
                    message("Bots not found!!!").
                    status(400).
                    success(false).
                    build();
        return ApiResponse.<Page<Bot>>builder().
                message("Bots here!!!").
                status(200).
                success(true).
                data(botPage).
                build();
    }

    public ApiResponse<Bot> getOne(Long id) {
        Optional<Bot> botOptional = botRepository.findById(id);
        if (botOptional.isEmpty()) {
            return ApiResponse.<Bot>builder().
                    message("Bot not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        return ApiResponse.<Bot>builder().
                message("Bot here!!!").
                status(200).
                success(true).
                data(botOptional.get()).
                build();
    }

    public ApiResponse<List<Bot>> getAllByCompany(Long companyId) {
        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if (companyOptional.isEmpty()) {
            return ApiResponse.<List<Bot>>builder().
                    message("Company not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        Company company = companyOptional.get();
        List<Bot> botList = company.getBotList();
        if (botList.isEmpty())
            return ApiResponse.<List<Bot>>builder().
                    message("Bots not found by this company!!!").
                    status(400).
                    success(false).
                    build();
        return ApiResponse.<List<Bot>>builder().
                message("Bots here!!!").
                status(200).
                success(true).
                data(botList).
                build();
    }

    @SneakyThrows
    public ApiResponse<Bot> add(BotDTO dto) {
        Optional<Company> companyOptional = companyRepository.findById(dto.getCompanyId());
        if (companyOptional.isEmpty())
            return ApiResponse.<Bot>builder().
                    message("Company not found!!!").
                    status(400).
                    success(false).
                    build();
        Bot bot = new Bot();
        bot.setCompany(companyOptional.get());
        bot.setActive(dto.isActive());
        bot.setToken(dto.getToken());
        bot.setUsername(dto.getUsername());
        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            MultipartFile photo = dto.getPhoto();
            Attachment attachment = new Attachment();
            attachment.setOriginalName(photo.getOriginalFilename());
            attachment.setBytes(photo.getBytes());
            attachment.setContentType(photo.getContentType());
            attachment.setSize(photo.getSize());
            bot.setLogo(attachment);
        }
        Bot save = botRepository.save(bot);
        return ApiResponse.<Bot>builder().
                message("Bot saved!!!").
                success(true).
                status(201).
                data(save).
                build();
    }


    @SneakyThrows
    public ApiResponse<Bot> edit(Long id, BotDTO dto) {
        Optional<Bot> botOptional = botRepository.findById(id);
        if (botOptional.isEmpty()) {
            return ApiResponse.<Bot>builder().
                    message("Bot not found!!!").
                    status(400).
                    success(false).
                    build();
        }
        Bot bot = botOptional.get();
        Optional<Company> companyOptional = companyRepository.findById(dto.getCompanyId());
        if (companyOptional.isEmpty())
            return ApiResponse.<Bot>builder().
                    message("Company not found!!!").
                    status(400).
                    success(false).
                    build();
        bot.setCompany(companyOptional.get());
        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            MultipartFile photo = dto.getPhoto();
            Attachment attachment = new Attachment();
            if (bot.getLogo() != null){
                attachment = bot.getLogo();
            }
            attachment.setOriginalName(photo.getOriginalFilename());
            attachment.setBytes(photo.getBytes());
            attachment.setContentType(photo.getContentType());
            attachment.setSize(photo.getSize());
            bot.setLogo(attachment);
        }
        bot.setActive(dto.isActive());
        bot.setToken(dto.getToken());
        bot.setUsername(dto.getUsername());
        Bot save = botRepository.save(bot);
        return ApiResponse.<Bot>builder().
                message("Bot edited!!!").
                success(true).
                status(200).
                data(save).
                build();
    }
}
