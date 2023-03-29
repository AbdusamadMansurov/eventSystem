package com.example.eventsystem.dto;

import com.example.eventsystem.model.enums.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private  Long id;
    private String text;
    private Long user_id;
    private Long request_id;
    private MessageType messageType;
    private LocalDateTime sendTime;
    private String email;

    public MessageDTO( String text, Long user_id, Long request_id, MessageType messageType, LocalDateTime sendTime, String email) {
        this.text = text;
        this.user_id = user_id;
        this.request_id = request_id;
        this.messageType = messageType;
        this.sendTime = sendTime;
        this.email = email;
    }
}
