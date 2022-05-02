package com.example.emailappusingquartz.controller;

import com.example.emailappusingquartz.dto.EmailRequest;
import com.example.emailappusingquartz.dto.EmailResponse;
import com.example.emailappusingquartz.service.EmailSchedulerServiceImpl;
import com.example.emailappusingquartz.service.ExceptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class EmailSchedulerController {

    @Autowired
    EmailSchedulerServiceImpl schedulerService;

    @Autowired
    ExceptionService exceptionService;

    @PostMapping("/schedule/email")
    public ResponseEntity<EmailResponse> scheduleEmail(@Validated @RequestBody EmailRequest request) {
        ResponseEntity<EmailResponse> response;
        try {
            response = schedulerService.scheduleEmail(request);
        } catch (Exception e) {
            log.error("Error while processing request: {}", e.getMessage());
            response = exceptionService.getErrorResponse(e);
        }
        log.info("created schedule trigger successfully");
        return response;
    }
}
