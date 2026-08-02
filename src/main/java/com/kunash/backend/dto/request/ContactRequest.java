package com.kunash.backend.dto.request;

import lombok.Data;

@Data
public class ContactRequest {
    private String name;
    private String phone;
    private String email;
    private String subject;
    private String message;
}