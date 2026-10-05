package com.clinicalx.notification.dto;

import com.clinicalx.notification.enums.NotificationEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmailNotificationRequest {

    private String to;

    private NotificationEvent notificationEvent;

    private Map<String, String> variables;
}