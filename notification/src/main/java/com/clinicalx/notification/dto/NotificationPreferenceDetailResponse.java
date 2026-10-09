
package com.clinicalx.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificationPreferenceDetailResponse {

    private Long id;
    private String notificationEvent;
    private Boolean whatsappEnabled;
    private Boolean smsEnabled;
    private Boolean emailEnabled;
}