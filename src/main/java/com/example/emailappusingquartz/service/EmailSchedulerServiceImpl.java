package com.example.emailappusingquartz.service;

import com.example.emailappusingquartz.dto.EmailRequest;
import com.example.emailappusingquartz.dto.EmailResponse;
import com.example.emailappusingquartz.quartzJob.EmailJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
public class EmailSchedulerServiceImpl {

    @Autowired
    private Scheduler scheduler;

    public ResponseEntity<EmailResponse> scheduleEmail(@Validated @RequestBody EmailRequest request){
        EmailResponse response;
        try {
            ZonedDateTime dateTime = ZonedDateTime.of(request.getDateTime(),request.getTimeZone());
            if(dateTime.isBefore(ZonedDateTime.now())){
                response = EmailResponse.builder()
                        .success(false)
                        .message("dateTime must be after current time")
                        .build();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(response);
            }
            JobDetail jobDetail = buildJobDetail(request);
            Trigger trigger = buildTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);
            response = EmailResponse.builder()
                    .success(true)
                    .jobId(jobDetail.getKey().getName())
                    .jobGroup(jobDetail.getKey().getGroup())
                    .message("Email scheduling successfully done")
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (SchedulerException e) {
            log.error("Error while scheduling email : ", e);
            response = EmailResponse.builder()
                    .success(false)
                    .message("Error while Scheduling email")
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
    private JobDetail buildJobDetail(EmailRequest request) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("email", request.getEmail());
        jobDataMap.put("subject", request.getSubject());
        jobDataMap.put("body", request.getBody());

        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "email-job")
                .withDescription("send Email Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime zonedDateTime) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "email-trigger")
                .startAt(Date.from(zonedDateTime.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }


}
