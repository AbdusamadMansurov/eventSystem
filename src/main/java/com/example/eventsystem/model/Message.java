package com.example.eventsystem.model;

import com.example.eventsystem.model.enums.MessageType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne
    private Bot bot;
    @ManyToOne
    private User user;
    @ManyToOne
    private Request request;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
    @Builder.Default
    private LocalDateTime sendTime = LocalDateTime.now();
    private LocalDateTime acceptTime;
    private boolean accept;
}
