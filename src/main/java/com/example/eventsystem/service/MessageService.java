package com.example.eventsystem.service;

import com.example.eventsystem.dto.ApiResponse;
import com.example.eventsystem.dto.MessageDTO;
import com.example.eventsystem.model.Employee;
import com.example.eventsystem.model.Message;
import com.example.eventsystem.model.Request;
import com.example.eventsystem.model.User;
import com.example.eventsystem.repository.EmployeeRepository;
import com.example.eventsystem.repository.MessageRepository;
import com.example.eventsystem.repository.RequestRepository;
import com.example.eventsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final RequestRepository requestRepository;

    public ApiResponse<List<Message>> getAll() {
        List<Message> list = messageRepository.findAll();
        return ApiResponse.<List<Message>>builder().
                status(200).
                success(true).
                message("Here").
                data(list).
                build();
    }

    public boolean editStatus(List<Long> list) {
        try {
            List<Message> messageList = messageRepository.findAllById(list);
            messageList.forEach((message) -> {
                message.setSendTime(LocalDateTime.now());
            });
            messageRepository.saveAll(messageList);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public ApiResponse<Page<Message>> getAllByEmployee(Employee employee, int page, int size) {
        Page<Message> messages;
        ApiResponse<Page<Message>> response = new ApiResponse<>();
        messages = messageRepository.findAllByEmployeeAndSendTime(employee, null, PageRequest.of(page, size));

        response.setMessage("Here!!!");
        response.setStatus(200);
        response.setSuccess(true);
        response.setData(messages);
        return response;
    }

    public ApiResponse<?> add(MessageDTO dto,Employee employee) {
        Message message = new Message();

        Optional<Employee> employeeOptional = employeeRepository.findById(employee.getId());

        if (!employeeOptional.isPresent()) {
            return ApiResponse.builder()
                    .message("Employee not found")
                    .success(false)
                    .status(404)
                    .build();
        }

        Optional<Request> request = requestRepository.findById(dto.getRequest_id());
        if (!request.isPresent()) {
            return ApiResponse.builder()
                    .message("Request not found")
                    .success(false)
                    .status(404)
                    .build();
        }

        Optional<User> userOptional = userRepository.findById(dto.getUser_id());
        if (!userOptional.isPresent()) {
            return ApiResponse.builder()
                    .message("User not found")
                    .status(404)
                    .success(false)
                    .build();
        }
        message.setRequest(request.get());
        message.setText(dto.getText());
        message.setMessageType(dto.getMessageType());
//        message.setSendTime(LocalDateTime.now());
        message.setUser(userOptional.get());
        message.setEmployee(employee);
        if (userOptional.get().getDepartment() != null && userOptional.get().getDepartment().getBot() != null)
            message.setBot(userOptional.get().getDepartment().getBot());
        Message save = messageRepository.save(message);
        if (save == null) {
            return ApiResponse.builder().
                    message("Unsaved")
                    .status(400)
                    .success(false)
                    .build();
        }
        return ApiResponse.builder().
                message("Saved").
                status(200).
                success(true).
                build();
    }
}
