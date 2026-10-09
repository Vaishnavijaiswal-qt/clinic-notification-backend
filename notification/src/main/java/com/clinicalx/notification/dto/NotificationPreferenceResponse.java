
package com.clinicalx.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NotificationPreferenceResponse {

    private Long clientId;
    private String clientName;
    private Long clinicId;
    private String clinicName;
    private List<NotificationPreferenceDetailResponse> preferences;
}