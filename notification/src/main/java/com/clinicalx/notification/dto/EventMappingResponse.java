package com.clinicalx.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventMappingResponse {

    private Long id;

    private Long clientId;
    private String clientName;

    private Long clinicId;
    private String clinicName;

    private Long eventId;
    private String eventName;

    private String notificationType;
}