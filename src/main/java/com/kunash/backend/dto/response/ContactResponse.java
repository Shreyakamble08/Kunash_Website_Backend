package com.kunash.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
}