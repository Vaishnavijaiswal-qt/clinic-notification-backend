package com.clinicalx.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
public class NotificationPreferenceItem {

    @NotBlank(message = "Notification event name is required")
    private String notificationEvent;

    @NotNull
    private Boolean whatsappEnabled;

    @NotNull
    private Boolean smsEnabled;

    @NotNull
    private Boolean emailEnabled;
}