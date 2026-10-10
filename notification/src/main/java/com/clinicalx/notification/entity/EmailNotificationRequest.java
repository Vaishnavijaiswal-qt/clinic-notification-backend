package com.clinicalx.notification.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class EmailNotificationRequest {

    private String to;

    private String notificationEvent;

    private Map<String, String> variables;
}