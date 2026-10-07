package com.clinicalx.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EventMappingUpdateResponse {

    private Long clientId;
    private Long clinicId;
    private Long eventId;
    private List<String> notificationTypes;
}