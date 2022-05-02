package com.example.emailappusingquartz.dto;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
public class EmailRequest {

    // TODO: email validation
    @NonNull
    private String email;

    @NonNull
    private String subject;

    @NonNull
    private String body;

    @NonNull
    private LocalDateTime dateTime;

    @NonNull
    private ZoneId timeZone;


}
