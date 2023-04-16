package com.example.emailappusingquartz.service;

import com.example.emailappusingquartz.dto.EmailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ExceptionService {

    public ResponseEntity<EmailResponse> getErrorResponse(Exception e) {
        EmailResponse response = EmailResponse.builder()
                .message("Unidentified Error")
                .success(false)
                .build();
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(response);
    }
}
